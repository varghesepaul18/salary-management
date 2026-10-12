import { Component, inject, output, signal } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatTooltipModule } from '@angular/material/tooltip';
import { Router, RouterLink, RouterLinkActive } from '@angular/router';
import { AuthService } from '../../core/auth/auth.service';

@Component({
  selector: 'app-workspace-sidebar',
  imports: [MatButtonModule, MatTooltipModule, RouterLink, RouterLinkActive],
  templateUrl: './workspace-sidebar.html',
  styleUrl: './workspace-sidebar.scss',
  host: {
    '[class.sidebar-collapsed]': 'collapsed()',
  },
})
export class WorkspaceSidebar {
  private readonly authService = inject(AuthService);
  private readonly router = inject(Router);

  protected readonly collapsed = signal(false);
  protected readonly mobileNavOpen = signal(false);
  protected readonly collapsedChange = output<boolean>();

  protected toggleSidebar(): void {
    this.collapsed.update((isCollapsed) => !isCollapsed);
    this.collapsedChange.emit(this.collapsed());
  }

  protected toggleMobileNav(): void {
    this.mobileNavOpen.update((isOpen) => !isOpen);
  }

  protected closeMobileNav(): void {
    this.mobileNavOpen.set(false);
  }

  protected signOut(): void {
    this.closeMobileNav();
    this.authService.logout();
    void this.router.navigateByUrl('/login');
  }
}
