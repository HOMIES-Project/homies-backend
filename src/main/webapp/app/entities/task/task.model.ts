import dayjs from 'dayjs/esm';
import { ITaskList } from 'app/entities/task-list/task-list.model';
import { IUserData } from 'app/entities/Homies/user-data/user-data.model';

export interface ITask {
  id?: number;
  taskName?: string;
  dataCreate?: dayjs.Dayjs | null;
  dataEnd?: dayjs.Dayjs | null;
  description?: string;
  cancel?: boolean | null;
  photoContentType?: string | null;
  photo?: string | null;
  puntuacion?: string | null;
  taskList?: ITaskList | null;
  userData?: IUserData | null;
  userCreator?: IUserData | null;
  userAssigneds?: IUserData[] | null;
}

export class Task implements ITask {
  constructor(
    public id?: number,
    public taskName?: string,
    public dataCreate?: dayjs.Dayjs | null,
    public dataEnd?: dayjs.Dayjs | null,
    public description?: string,
    public cancel?: boolean | null,
    public photoContentType?: string | null,
    public photo?: string | null,
    public puntuacion?: string | null,
    public taskList?: ITaskList | null,
    public userData?: IUserData | null,
    public userCreator?: IUserData | null,
    public userAssigneds?: IUserData[] | null
  ) {
    this.cancel = this.cancel ?? false;
  }
}

export function getTaskIdentifier(task: ITask): number | undefined {
  return task.id;
}
