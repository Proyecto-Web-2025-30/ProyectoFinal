import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService, User } from '../../services/auth.service';
import { RoleService, Role } from '../../services/role.service';
import { NavbarComponent } from '../navbar/navbar.component';

@Component({
  selector: 'app-roles',
  standalone: true,
  imports: [CommonModule, FormsModule, NavbarComponent],
  templateUrl: './roles.component.html'
})
export class RolesComponent implements OnInit {
  currentUser: User | null = null;
  roles: Role[] = [];
  isLoading = false;
  showForm = false;
  editingRole: Role | null = null;

  name = '';
  description = '';

  constructor(
    private authService: AuthService,
    private roleService: RoleService,
    private router: Router
  ) {}

  ngOnInit() {
    this.currentUser = this.authService.getCurrentUser();
    if (!this.currentUser) {
      this.router.navigate(['/login']);
      return;
    }
    this.loadRoles();
  }

  loadRoles() {
    if (!this.currentUser) return;
    this.isLoading = true;
    this.roleService.getRolesByCompany(this.currentUser.companyId).subscribe({
      next: (data) => {
        this.roles = data;
        this.isLoading = false;
      },
      error: () => {
        this.isLoading = false;
      }
    });
  }

  showCreateForm() {
    this.showForm = true;
    this.editingRole = null;
    this.name = '';
    this.description = '';
  }

  showEditForm(role: Role) {
    this.showForm = true;
    this.editingRole = role;
    this.name = role.name;
    this.description = role.description;
  }

  cancelForm() {
    this.showForm = false;
    this.editingRole = null;
  }

  saveRole() {
    if (!this.currentUser) return;

    const role: Role = {
      name: this.name,
      description: this.description
    };

    if (this.editingRole) {
      this.roleService.updateRole(this.editingRole.id!, role).subscribe({
        next: () => {
          this.loadRoles();
          this.cancelForm();
        },
        error: (err) => alert(err.error?.error || 'Error updating role')
      });
    } else {
      this.roleService.createRole(this.currentUser.companyId, role).subscribe({
        next: (response: any) => {
          console.log('Role created:', response);
          alert(`✅ Success! Role "${role.name}" created successfully.`);
          this.loadRoles();
          this.cancelForm();
        },
        error: (err) => {
          console.error('Error creating role:', err);
          const errorMsg = err.error?.details || err.error?.error || 'Unknown error occurred';
          alert(`❌ Error creating role:\n${errorMsg}\n\nMake sure both name and description are filled.`);
        }
      });
    }
  }

  deleteRole(role: Role) {
    if (!confirm(`Delete role "${role.name}"?`)) return;
    
    this.roleService.deleteRole(role.id!).subscribe({
      next: () => {
        this.loadRoles();
      },
      error: (err) => alert(err.error?.error || 'Error deleting role')
    });
  }

  goBack() {
    this.router.navigate(['/dashboard']);
  }
}
