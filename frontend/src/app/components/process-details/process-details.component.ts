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
  templateUrl: './process-details.component.html'
})
export class ProcessDetailsComponent implements OnInit {
  processId!: number;
  process: Process | null = null;
  activities: Activity[] = [];
  gateways: Gateway[] = [];
  arcs: Arc[] = [];
  roles: Role[] = [];
  
  activeTab: 'activities' | 'gateways' | 'arcs' = 'activities';
  
  // Activity form
  showActivityForm = false;
  activityName = '';
  activityType = '';
  activityDescription = '';
  activityRoleId: number | null = null;
  editingActivity: Activity | null = null;
  
  // Gateway form
  showGatewayForm = false;
  gatewayName = '';
  gatewayType: 'EXCLUSIVE' | 'PARALLEL' | 'INCLUSIVE' = 'EXCLUSIVE';
  gatewayConditions = '';
  editingGateway: Gateway | null = null;
  
  // Arc form
  showArcForm = false;
  arcSourceType = 'ACTIVITY';
  arcSourceId: number | null = null;
  arcTargetType = 'ACTIVITY';
  arcTargetId: number | null = null;
  editingArc: Arc | null = null;

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

  // Activity methods
  showCreateActivityForm() {
    this.showActivityForm = true;
    this.editingActivity = null;
    this.activityName = '';
    this.activityType = '';
    this.activityDescription = '';
    this.activityRoleId = null;
  }

  showEditActivityForm(activity: Activity) {
    this.showActivityForm = true;
    this.editingActivity = activity;
    this.activityName = activity.name;
    this.activityType = activity.activityType;
    this.activityDescription = activity.description;
    this.activityRoleId = activity.roleId || null;
  }

  saveActivity() {
    const activity: Activity = {
      name: this.activityName,
      activityType: this.activityType,
      description: this.activityDescription,
      roleId: this.activityRoleId || undefined
    };

    if (this.editingActivity) {
      this.activityService.updateActivity(this.editingActivity.id!, activity).subscribe({
        next: () => {
          this.loadActivities();
          this.cancelActivityForm();
        }
      });
    } else {
      this.activityService.createActivity(this.processId, activity).subscribe({
        next: () => {
          this.loadActivities();
          this.cancelActivityForm();
        }
      });
    }
  }

  deleteActivity(activity: Activity) {
    if (!confirm(`Delete activity "${activity.name}"?`)) return;
    this.activityService.deleteActivity(activity.id!).subscribe({
      next: () => this.loadActivities()
    });
  }

  cancelActivityForm() {
    this.showActivityForm = false;
    this.editingActivity = null;
  }

  // Gateway methods
  showCreateGatewayForm() {
    this.showGatewayForm = true;
    this.editingGateway = null;
    this.gatewayName = '';
    this.gatewayType = 'EXCLUSIVE';
    this.gatewayConditions = '';
  }

  showEditGatewayForm(gateway: Gateway) {
    this.showGatewayForm = true;
    this.editingGateway = gateway;
    this.gatewayName = gateway.name;
    this.gatewayType = gateway.gatewayType;
    this.gatewayConditions = gateway.conditions;
  }

  saveGateway() {
    const gateway: Gateway = {
      name: this.gatewayName,
      gatewayType: this.gatewayType,
      conditions: this.gatewayConditions
    };

    if (this.editingGateway) {
      this.gatewayService.updateGateway(this.editingGateway.id!, gateway).subscribe({
        next: () => {
          this.loadGateways();
          this.cancelGatewayForm();
        }
      });
    } else {
      this.gatewayService.createGateway(this.processId, gateway).subscribe({
        next: () => {
          this.loadGateways();
          this.cancelGatewayForm();
        }
      });
    }
  }

  deleteGateway(gateway: Gateway) {
    if (!confirm(`Delete gateway "${gateway.name}"?`)) return;
    this.gatewayService.deleteGateway(gateway.id!).subscribe({
      next: () => this.loadGateways()
    });
  }

  cancelGatewayForm() {
    this.showGatewayForm = false;
    this.editingGateway = null;
  }

  // Arc methods
  showCreateArcForm() {
    this.showArcForm = true;
    this.editingArc = null;
    this.arcSourceType = 'ACTIVITY';
    this.arcSourceId = null;
    this.arcTargetType = 'ACTIVITY';
    this.arcTargetId = null;
  }

  showEditArcForm(arc: Arc) {
    this.showArcForm = true;
    this.editingArc = arc;
    this.arcSourceType = arc.sourceType;
    this.arcSourceId = arc.sourceId;
    this.arcTargetType = arc.targetType;
    this.arcTargetId = arc.targetId;
  }

  saveArc() {
    const arc: Arc = {
      sourceType: this.arcSourceType,
      sourceId: this.arcSourceId!,
      targetType: this.arcTargetType,
      targetId: this.arcTargetId!
    };

    if (this.editingArc) {
      this.arcService.updateArc(this.editingArc.id!, arc).subscribe({
        next: () => {
          this.loadArcs();
          this.cancelArcForm();
        }
      });
    } else {
      this.arcService.createArc(this.processId, arc).subscribe({
        next: () => {
          this.loadArcs();
          this.cancelArcForm();
        }
      });
    }
  }

  deleteArc(arc: Arc) {
    if (!confirm('Delete this arc?')) return;
    this.arcService.deleteArc(arc.id!).subscribe({
      next: () => this.loadArcs()
    });
  }

  cancelArcForm() {
    this.showArcForm = false;
    this.editingArc = null;
  }

  goBack() {
    this.router.navigate(['/processes']);
  }
}
