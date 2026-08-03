import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'b64toImg',
})

export class B64toImgPipe implements PipeTransform {
  transform(rawData: string, MIMEtype: string): string {
    return `data:${MIMEtype};base64,${rawData}`;
  }
}
