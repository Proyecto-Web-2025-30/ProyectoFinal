import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { AuthService, User } from '../../services/auth.service';
import { ProcessService, Process } from '../../services/process.service';
import { RoleService, Role } from '../../services/role.service';
import { NavbarComponent } from '../navbar/navbar.component';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, NavbarComponent],
  templateUrl: './dashboard.component.html'
})
export class DashboardComponent implements OnInit {
  currentUser: User | null = null;
  processes: Process[] = [];
  roles: Role[] = [];
  isLoading = false;

  constructor(
    private authService: AuthService,
    private processService: ProcessService,
    private roleService: RoleService,
    private router: Router
  ) {}

  ngOnInit() {
    this.currentUser = this.authService.getCurrentUser();
    if (!this.currentUser) {
      this.router.navigate(['/login']);
      return;
    }
    this.loadData();
  }

  loadData() {
    if (!this.currentUser) return;
    
    this.isLoading = true;
    this.processService.getProcessesByCompany(this.currentUser.companyId).subscribe({
      next: (data) => {
        this.processes = data;
        this.isLoading = false;
      },
      error: () => {
        this.isLoading = false;
      }
    });

    this.roleService.getRolesByCompany(this.currentUser.companyId).subscribe({
      next: (data) => {
        this.roles = data;
      }
    });
  }

  logout() {
    this.authService.logout();
    this.router.navigate(['/login']);
  }

  goToProcesses() {
    this.router.navigate(['/processes']);
  }

  goToRoles() {
    this.router.navigate(['/roles']);
  }

  goToUsers() {
    this.router.navigate(['/users']);
  }
}
