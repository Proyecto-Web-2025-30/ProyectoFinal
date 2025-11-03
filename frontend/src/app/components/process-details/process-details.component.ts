import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { ProcessService, Process } from '../../services/process.service';
import { ActivityService, Activity } from '../../services/activity.service';
import { GatewayService, Gateway } from '../../services/gateway.service';
import { ArcService, Arc } from '../../services/arc.service';
import { RoleService, Role } from '../../services/role.service';
import { AuthService } from '../../services/auth.service';
import { NavbarComponent } from '../navbar/navbar.component';

@Component({
  selector: 'app-process-details',
  standalone: true,
  imports: [CommonModule, FormsModule, NavbarComponent],
  templateUrl: './process-details.component.html',
  styleUrls: ['./process-details.component.css']
})
export class ProcessDetailsComponent implements OnInit {
  processId!: number;
  process: Process | null = null;
  activities: Activity[] = [];
  gateways: Gateway[] = [];
  arcs: Arc[] = [];
  roles: Role[] = [];

  // Selection & forms
  selected: { type: 'ACTIVITY' | 'GATEWAY'; id: number } | null = null;
  activityForm: Partial<Activity> = {};
  gatewayForm: Partial<Gateway> = {};
  pendingArcSource: { type: 'ACTIVITY' | 'GATEWAY'; id: number } | null = null;
  dragging: {
    type: 'ACTIVITY' | 'GATEWAY';
    id: number;
    offsetX: number;
    offsetY: number;
  } | null = null;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private processService: ProcessService,
    private activityService: ActivityService,
    private gatewayService: GatewayService,
    private arcService: ArcService,
    private roleService: RoleService,
    private authService: AuthService
  ) {}

  ngOnInit() {
    this.processId = Number(this.route.snapshot.paramMap.get('id'));
    this.loadProcessDetails();
    this.loadRoles();
  }

  loadProcessDetails() {
    this.processService.getProcess(this.processId).subscribe({
      next: (data) => this.process = data
    });
    console.log(this.process);
    this.loadActivities();
    this.loadGateways();
    this.loadArcs();
  }

  loadActivities() {
    this.activityService.getActivitiesByProcess(this.processId).subscribe({
      next: (data) => this.activities = data
    });
  }

  loadGateways() {
    this.gatewayService.getGatewaysByProcess(this.processId).subscribe({
      next: (data) => this.gateways = data
    });
  }

  loadArcs() {
    this.arcService.getArcsByProcess(this.processId).subscribe({
      next: (data) => this.arcs = data
    });
  }

  loadRoles() {
    const user = this.authService.getCurrentUser();
    if (user) {
      this.roleService.getRolesByCompany(user.companyId).subscribe({
        next: (data) => this.roles = data
      });
    }
  }
  // Palette drag start
  onPaletteDragStart(event: DragEvent, payload: string) {
    event.dataTransfer?.setData('text/plain', payload);
  }

  onCanvasDragOver(event: DragEvent) {
    event.preventDefault();
  }

  onCanvasDrop(event: DragEvent) {
    event.preventDefault();
    const payload = event.dataTransfer?.getData('text/plain');
    if (!payload) return;

    const canvasRect = (event.currentTarget as HTMLElement).getBoundingClientRect();
    const x = event.clientX - canvasRect.left;
    const y = event.clientY - canvasRect.top;

    if (payload === 'ACTIVITY') {
      const activity: Activity = {
        name: 'Activity', activityType: '', description: '', x, y
      };
      this.activityService.createActivity(this.processId, activity).subscribe({
        next: (created) => {
          this.activities = [...this.activities, created];
          this.selectNode('ACTIVITY', created.id!);
        }
      });
    } else if (payload.startsWith('GATEWAY:')) {
      const type = payload.split(':')[1] as Gateway['gatewayType'];
      const gateway: Gateway = {
        name: type + ' Gateway', gatewayType: type, conditions: '', x, y
      };
      this.gatewayService.createGateway(this.processId, gateway).subscribe({
        next: (created) => {
          this.gateways = [...this.gateways, created];
          this.selectNode('GATEWAY', created.id!);
        }
      });
    }
  }

  onCanvasClick(event: MouseEvent) {
    if ((event.target as HTMLElement).classList.contains('canvas')) {
      this.selected = null;
      this.pendingArcSource = null;
    }
  }

  onNodeMouseDown(event: MouseEvent, type: 'ACTIVITY' | 'GATEWAY', node: Activity | Gateway) {
    event.preventDefault();
    const canvas = (event.currentTarget as HTMLElement).closest('.canvas') as HTMLElement | null;
    const canvasRect = canvas ? canvas.getBoundingClientRect() : (document.querySelector('.canvas') as HTMLElement).getBoundingClientRect();
    const nodeX = (node as any).x || 40;
    const nodeY = (node as any).y || 40;
    const mouseX = event.clientX - canvasRect.left;
    const mouseY = event.clientY - canvasRect.top;
    this.dragging = {
      type,
      id: (node as any).id,
      offsetX: mouseX - nodeX,
      offsetY: mouseY - nodeY
    };

    // Bind listeners on window to allow smooth dragging outside node element
    window.addEventListener('mousemove', this.onWindowMouseMove);
    window.addEventListener('mouseup', this.onWindowMouseUp);
  }

  onWindowMouseMove = (event: MouseEvent) => {
    if (!this.dragging) return;
    const canvas = document.querySelector('.canvas') as HTMLElement;
    const rect = canvas.getBoundingClientRect();
    const mouseX = event.clientX - rect.left;
    const mouseY = event.clientY - rect.top;
    const newX = Math.max(0, mouseX - this.dragging.offsetX);
    const newY = Math.max(0, mouseY - this.dragging.offsetY);

    if (this.dragging.type === 'ACTIVITY') {
      const idx = this.activities.findIndex(a => a.id === this.dragging!.id);
      if (idx >= 0) {
        this.activities[idx] = { ...this.activities[idx], x: newX, y: newY };
      }
    } else {
      const idx = this.gateways.findIndex(g => g.id === this.dragging!.id);
      if (idx >= 0) {
        this.gateways[idx] = { ...this.gateways[idx], x: newX, y: newY };
      }
    }
  };

  onWindowMouseUp = () => {
    if (!this.dragging) return;
    const { type, id } = this.dragging;
    this.dragging = null;
    window.removeEventListener('mousemove', this.onWindowMouseMove);
    window.removeEventListener('mouseup', this.onWindowMouseUp);

    if (type === 'ACTIVITY') {
      const a = this.activities.find(x => x.id === id);
      if (a) {
        const payload: Activity = {
          name: a.name,
          activityType: a.activityType,
          description: a.description,
          x: a.x, y: a.y
        };
        this.activityService.updateActivity(a.id!, payload).subscribe();
      }
    } else {
      const g = this.gateways.find(x => x.id === id);
      if (g) {
        const payload: Gateway = {
          name: g.name,
          gatewayType: g.gatewayType,
          conditions: g.conditions,
          x: g.x, y: g.y
        };
        this.gatewayService.updateGateway(g.id!, payload).subscribe();
      }
    }
  };

  onNodeClick(event: MouseEvent, type: 'ACTIVITY' | 'GATEWAY', node: Activity | Gateway) {
    event.stopPropagation();
    if (event.shiftKey) {
      if (!this.pendingArcSource) {
        this.pendingArcSource = { type, id: (node as any).id };
      } else {
        const source = this.pendingArcSource;
        const target = { type, id: (node as any).id };
        if (!(source.type === target.type && source.id === target.id)) {
          const existing = this.arcs.find(a => a.sourceType === source.type && a.sourceId === source.id && a.targetType === target.type && a.targetId === target.id);
          const reverse = this.arcs.find(a => a.sourceType === target.type && a.sourceId === target.id && a.targetType === source.type && a.targetId === source.id);

          const createNew = () => {
            this.arcService.createArc(this.processId, {
              sourceType: source.type,
              sourceId: source.id,
              targetType: target.type,
              targetId: target.id
            }).subscribe({ next: (created) => {
              this.arcs = [...this.arcs, created];
              this.pendingArcSource = null;
            }});
          };

          if (existing && existing.id != null) {
            // Toggle: delete same-direction arc
            const ok = confirm('Delete existing arc between these nodes?');
            if (!ok) { this.pendingArcSource = null; return; }
            this.arcService.deleteArc(existing.id).subscribe({ next: () => {
              this.arcs = this.arcs.filter(a => a.id !== existing.id);
              this.pendingArcSource = null;
            }});
          } else if (reverse && reverse.id != null) {
            // If reverse arc exists, delete it first, then create the new one
            const ok = confirm('A reverse arc exists. Replace it with the new direction?');
            if (!ok) { this.pendingArcSource = null; return; }
            this.arcService.deleteArc(reverse.id).subscribe({ next: () => {
              this.arcs = this.arcs.filter(a => a.id !== reverse.id);
              createNew();
            }});
          } else {
            createNew();
          }
        }
      }
      return;
    }
    this.selectNode(type, (node as any).id);
  }

  selectNode(type: 'ACTIVITY' | 'GATEWAY', id: number) {
    this.selected = { type, id };
    if (type === 'ACTIVITY') {
      const a = this.activities.find(x => x.id === id)!;
      this.activityForm = { name: a.name, activityType: a.activityType, description: a.description };
      this.gatewayForm = {};
    } else {
      const g = this.gateways.find(x => x.id === id)!;
      this.gatewayForm = { name: g.name, gatewayType: g.gatewayType, conditions: g.conditions };
      this.activityForm = {};
    }
  }

  isSelected(type: 'ACTIVITY' | 'GATEWAY', id?: number) {
    return !!this.selected && this.selected.type === type && this.selected.id === id;
  }

  saveSelected() {
    if (!this.selected) return;
    if (this.selected.type === 'ACTIVITY') {
      const a = this.activities.find(x => x.id === this.selected!.id)!;
      const payload: Activity = {
        name: this.activityForm.name || a.name,
        activityType: this.activityForm.activityType || a.activityType,
        description: this.activityForm.description || a.description,
        x: a.x, y: a.y
      };
      this.activityService.updateActivity(a.id!, payload).subscribe({ next: (updated) => {
        this.loadActivities();
      }});
    } else {
      const g = this.gateways.find(x => x.id === this.selected!.id)!;
      const payload: Gateway = {
        name: this.gatewayForm.name || g.name,
        gatewayType: (this.gatewayForm.gatewayType || g.gatewayType) as Gateway['gatewayType'],
        conditions: this.gatewayForm.conditions || g.conditions,
        x: g.x, y: g.y
      };
      this.gatewayService.updateGateway(g.id!, payload).subscribe({ next: () => {
        this.loadGateways();
      }});
    }
  }

  deleteSelected() {
    if (!this.selected) return;
    if (this.selected.type === 'ACTIVITY') {
      const confirmDelete = confirm('Delete this activity and its connected arcs?');
      if (!confirmDelete) return;
      this.activityService.deleteActivity(this.selected.id).subscribe({ next: () => {
        this.activities = this.activities.filter(a => a.id !== this.selected!.id);
        this.arcs = this.arcs.filter(e => !(e.sourceType === 'ACTIVITY' && e.sourceId === this.selected!.id) && !(e.targetType === 'ACTIVITY' && e.targetId === this.selected!.id));
        this.selected = null;
      }});
    } else {
      const confirmDelete = confirm('Delete this gateway and its connected arcs?');
      if (!confirmDelete) return;
      this.gatewayService.deleteGateway(this.selected.id).subscribe({ next: () => {
        this.gateways = this.gateways.filter(g => g.id !== this.selected!.id);
        this.arcs = this.arcs.filter(e => !(e.sourceType === 'GATEWAY' && e.sourceId === this.selected!.id) && !(e.targetType === 'GATEWAY' && e.targetId === this.selected!.id));
        this.selected = null;
      }});
    }
  }

  getNodeCenterX(type: 'ACTIVITY' | 'GATEWAY' | string, id: number | null | undefined) {
    if (!id) return 0;
    const node = type === 'ACTIVITY' ? this.activities.find(a => a.id === id) : this.gateways.find(g => g.id === id);
    const halfWidth = type === 'GATEWAY' ? 18 : 50;
    const x = (node?.x || 40) + halfWidth;
    return x;
    }

  getNodeCenterY(type: 'ACTIVITY' | 'GATEWAY' | string, id: number | null | undefined) {
    if (!id) return 0;
    const node = type === 'ACTIVITY' ? this.activities.find(a => a.id === id) : this.gateways.find(g => g.id === id);
    const halfHeight = type === 'GATEWAY' ? 18 : 20;
    const y = (node?.y || 40) + halfHeight;
    return y;
  }

  private getNodeEdgePoint(
    type: 'ACTIVITY' | 'GATEWAY' | string,
    id: number | null | undefined,
    towardType: 'ACTIVITY' | 'GATEWAY' | string,
    towardId: number | null | undefined
  ) {
    const cx = this.getNodeCenterX(type, id);
    const cy = this.getNodeCenterY(type, id);
    const tx = this.getNodeCenterX(towardType, towardId);
    const ty = this.getNodeCenterY(towardType, towardId);
    let dx = tx - cx;
    let dy = ty - cy;
    if (dx === 0 && dy === 0) return { x: cx, y: cy };

    if (type === 'ACTIVITY') {
      const halfW = 50;
      const halfH = 20;
      const tX = dx === 0 ? Number.POSITIVE_INFINITY : Math.abs(halfW / dx);
      const tY = dy === 0 ? Number.POSITIVE_INFINITY : Math.abs(halfH / dy);
      const t = Math.min(tX, tY);
      return { x: cx + dx * t, y: cy + dy * t };
    } else {
      // Diamond with |x'| + |y'| = b
      const b = 18;
      const denom = Math.abs(dx) + Math.abs(dy);
      const t = denom === 0 ? 0 : b / denom;
      return { x: cx + dx * t, y: cy + dy * t };
    }
  }

  getEdgeX(type: 'ACTIVITY' | 'GATEWAY' | string, id: number | null | undefined, towardType: 'ACTIVITY' | 'GATEWAY' | string, towardId: number | null | undefined) {
    return this.getNodeEdgePoint(type, id, towardType, towardId).x;
  }

  getEdgeY(type: 'ACTIVITY' | 'GATEWAY' | string, id: number | null | undefined, towardType: 'ACTIVITY' | 'GATEWAY' | string, towardId: number | null | undefined) {
    return this.getNodeEdgePoint(type, id, towardType, towardId).y;
  }
}
