import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService, User } from '../../services/auth.service';
import { ProcessService, Process } from '../../services/process.service';
import { NavbarComponent } from '../navbar/navbar.component';

@Component({
  selector: 'app-processes',
  standalone: true,
  imports: [CommonModule, FormsModule, NavbarComponent],
  templateUrl: './processes.component.html'
})
export class ProcessesComponent implements OnInit {
  currentUser: User | null = null;
  processes: Process[] = [];
  isLoading = false;
  showForm = false;
  editingProcess: Process | null = null;

  // Form fields
  name = '';
  description = '';
  category = '';
  status: 'DRAFT' | 'PUBLISHED' = 'DRAFT';

  constructor(
    private authService: AuthService,
    private processService: ProcessService,
    private router: Router
  ) {}

  ngOnInit() {
    this.currentUser = this.authService.getCurrentUser();
    if (!this.currentUser) {
      this.router.navigate(['/login']);
      return;
    }
    this.loadProcesses();
  }

  loadProcesses() {
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
  }

  showCreateForm() {
    this.showForm = true;
    this.editingProcess = null;
    this.resetForm();
  }

  showEditForm(process: Process) {
    this.showForm = true;
    this.editingProcess = process;
    this.name = process.name;
    this.description = process.description;
    this.category = process.category;
    this.status = process.status;
  }

  resetForm() {
    this.name = '';
    this.description = '';
    this.category = '';
    this.status = 'DRAFT';
  }

  cancelForm() {
    this.showForm = false;
    this.editingProcess = null;
    this.resetForm();
  }

  saveProcess() {
    if (!this.currentUser) return;

    const process: Process = {
      name: this.name,
      description: this.description,
      category: this.category,
      status: this.status
    };

    if (this.editingProcess) {
      this.processService.updateProcess(this.editingProcess.id!, process).subscribe({
        next: () => {
          this.loadProcesses();
          this.cancelForm();
        },
        error: (err) => alert(err.error?.error || 'Error updating process')
      });
    } else {
      this.processService.createProcess(this.currentUser.companyId, process).subscribe({
        next: (response: any) => {
          console.log('Process created:', response);
          alert(`✅ Success! Process "${process.name}" created successfully.`);
          this.loadProcesses();
          this.cancelForm();
        },
        error: (err) => {
          console.error('Error creating process:', err);
          const errorMsg = err.error?.details || err.error?.error || 'Unknown error occurred';
          alert(`❌ Error creating process:\n${errorMsg}\n\nPlease check the console for more details.`);
        }
      });
    }
  }

  deleteProcess(process: Process) {
    if (!confirm(`Delete process "${process.name}"?`)) return;
    
    this.processService.deleteProcess(process.id!).subscribe({
      next: () => {
        this.loadProcesses();
      },
      error: (err) => alert(err.error?.error || 'Error deleting process')
    });
  }

  viewProcessDetails(process: Process) {
    this.router.navigate(['/process-details', process.id]);
  }

  goBack() {
    this.router.navigate(['/dashboard']);
  }
}
