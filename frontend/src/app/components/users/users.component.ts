import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService, User } from '../../services/auth.service';
import { UserService, CreateUserRequest, AppUser } from '../../services/user.service';
import { NavbarComponent } from '../navbar/navbar.component';

@Component({
  selector: 'app-users',
  standalone: true,
  imports: [CommonModule, FormsModule, NavbarComponent],
  templateUrl: './users.component.html'
})
export class UsersComponent implements OnInit {
  currentUser: User | null = null;
  companyId!: number;

  users: AppUser[] = [];
  isLoading = false;
  showForm = false;

  username = '';
  email = '';
  password = '';
  fullName = '';
  userRole = 'EDITOR';

  constructor(
    private authService: AuthService,
    private userService: UserService,
    private router: Router
  ) {}

  ngOnInit() {
    // Fallback a localStorage para soportar refresh a las malas 
    this.currentUser = this.authService.getCurrentUser()
      ?? JSON.parse(localStorage.getItem('currentUser') || 'null');

    if (!this.currentUser) {
      this.router.navigate(['/login']);
      return;
    }

    // Establecemos el companyId como número
    this.companyId = Number(this.currentUser.companyId);
    if (Number.isNaN(this.companyId)) {
      console.error('CompanyId inválido');
      this.router.navigate(['/login']);
      return;
    }

    this.loadUsers();
  }

  private handleError(e: any) {
    console.error('Users error:', e);
    this.isLoading = false;
    this.users = [];
  }

  loadUsers() {
    this.isLoading = true;
    this.userService.getUsersByCompany(this.companyId).subscribe({
      next: (data) => {
        // Por si el backend devolviera null 
        this.users = Array.isArray(data) ? data : [];
        this.isLoading = false;
      },
      error: (e) => this.handleError(e)
    });
  }

  showCreateForm() {
    this.showForm = true;
    this.username = '';
    this.email = '';
    this.password = '';
    this.fullName = '';
    this.userRole = 'EDITOR';
  }

  cancelForm() {
    this.showForm = false;
  }

  saveUser() {
    const payload: CreateUserRequest = {
      username: this.username.trim(),
      email: this.email.trim(),
      password: this.password,
      fullName: this.fullName.trim(),
      userRole: this.userRole
    };

    this.userService.createUser(this.companyId, payload).subscribe({
      next: () => {
        this.loadUsers();   // Refresca la lista papi
        this.cancelForm();
      },
      error: (err) => alert(err?.error?.error || 'Error creating user')
    });
  }

  goBack() {
    this.router.navigate(['/dashboard']);
  }
}
