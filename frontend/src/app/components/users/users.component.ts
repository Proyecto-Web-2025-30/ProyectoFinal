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
    this.currentUser = this.authService.getCurrentUser();
    if (!this.currentUser) {
      this.router.navigate(['/login']);
      return;
    }
    this.loadUsers();
  }

  loadUsers() {
    if (!this.currentUser) return;
    this.isLoading = true;
    this.userService.getUsersByCompany(this.currentUser.companyId).subscribe({
      next: (data) => {
        this.users = data;
        this.isLoading = false;
      },
      error: () => {
        this.isLoading = false;
      }
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
    if (!this.currentUser) return;

    const user: CreateUserRequest = {
      username: this.username,
      email: this.email,
      password: this.password,
      fullName: this.fullName,
      userRole: this.userRole
    };

    this.userService.createUser(this.currentUser.companyId, user).subscribe({
      next: () => {
        this.loadUsers();
        this.cancelForm();
      },
      error: (err) => alert(err.error?.error || 'Error creating user')
    });
  }

  goBack() {
    this.router.navigate(['/dashboard']);
  }
}
