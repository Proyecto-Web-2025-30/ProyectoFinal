import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AuthService, RegisterCompanyRequest } from '../../services/auth.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.css']
})
export class RegisterComponent {
  // User fields
  adminFullName = '';
  lastName = '';
  adminEmail = '';
  username = '';
  adminPassword = '';
  confirmPassword = '';
  
  // Company fields
  companyName = '';
  nit = '';
  contactEmail = '';
  
  // Checkboxes
  acceptPrivacy = false;
  acceptTerms = false;
  acceptNotifications = false;
  
  errorMessage = '';
  successMessage = '';
  isLoading = false;

  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  onRegister() {
    if (!this.adminFullName || !this.lastName || !this.adminEmail || 
        !this.username || !this.adminPassword || !this.confirmPassword ||
        !this.companyName || !this.nit || !this.contactEmail) {
      this.errorMessage = 'Por favor completa todos los campos';
      return;
    }

    if (this.adminPassword !== this.confirmPassword) {
      this.errorMessage = 'Las contraseñas no coinciden';
      return;
    }

    if (!this.acceptPrivacy || !this.acceptTerms) {
      this.errorMessage = 'Debes aceptar la Política de Privacidad y los Términos y Condiciones';
      return;
    }

    this.isLoading = true;
    this.errorMessage = '';
    this.successMessage = '';

    const fullName = `${this.adminFullName} ${this.lastName}`;
    
    const request: RegisterCompanyRequest = {
      companyName: this.companyName,
      nit: this.nit,
      contactEmail: this.contactEmail,
      adminUsername: this.username,
      adminEmail: this.adminEmail,
      adminPassword: this.adminPassword,
      adminFullName: fullName
    };

    this.authService.registerCompany(request).subscribe({
      next: () => {
        this.successMessage = 'Company registered successfully! Redirecting to login...';
        setTimeout(() => {
          this.router.navigate(['/login']);
        }, 2000);
      },
      error: (error) => {
        this.errorMessage = error.error?.error || 'Registration failed';
        this.isLoading = false;
      }
    });
  }

  goToLogin() {
    this.router.navigate(['/login']);
  }
}
