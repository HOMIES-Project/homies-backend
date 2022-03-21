import { IGroup } from 'app/entities/Homies/group/group.model';
import { ITask } from 'app/entities/task/task.model';

export interface ITaskList {
  id?: number;
  nameList?: string | null;
  group?: IGroup | null;
  tasks?: ITask[] | null;
}

export class TaskList implements ITaskList {
  constructor(public id?: number, public nameList?: string | null, public group?: IGroup | null, public tasks?: ITask[] | null) {}
}

export function getTaskListIdentifier(taskList: ITaskList): number | undefined {
  return taskList.id;
}
