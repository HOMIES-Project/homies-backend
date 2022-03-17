import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { ITaskList } from '../task-list.model';
import { TaskListService } from '../service/task-list.service';

@Component({
  templateUrl: './task-list-delete-dialog.component.html',
})
export class TaskListDeleteDialogComponent {
  taskList?: ITaskList;

  constructor(protected taskListService: TaskListService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.taskListService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
