export interface ITaskList {
  id?: number;
  nameList?: string | null;
}

export class TaskList implements ITaskList {
  constructor(public id?: number, public nameList?: string | null) {}
}

export function getTaskListIdentifier(taskList: ITaskList): number | undefined {
  return taskList.id;
}
