import dayjs from 'dayjs/esm';

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
    public puntuacion?: string | null
  ) {
    this.cancel = this.cancel ?? false;
  }
}

export function getTaskIdentifier(task: ITask): number | undefined {
  return task.id;
}
