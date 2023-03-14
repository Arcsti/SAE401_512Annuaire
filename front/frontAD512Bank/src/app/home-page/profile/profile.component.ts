import { AfterViewInit, Component, OnInit, ViewChild, ElementRef, HostListener, ApplicationRef, Injector } from '@angular/core';

enum Status {
  connected,
  diconnected
}

@Component({
  selector: 'app-profile',
  templateUrl: './profile.component.html',
  styleUrls: ['./profile.component.css']
})
export class ProfileComponent {
  status = Status.diconnected
  menuShown = false;

  imageLink = "";
  name = "Jean";
  surname = "Paul";
  letters : string = this.name.charAt(0) + this.surname.charAt(0);

  visibility = {
    "profilePicture": false,
    "connexion": true,
    "infoMenu": false
  }

  @HostListener('document:click', ['$event'])
  onDocumentClick(event: MouseEvent) {
    let target : HTMLElement = event.target as HTMLElement;
    if(target.id !== "connexionButton"){
      this.visibility["infoMenu"] = false;
      this.menuShown = false;
    }
  }

  connexionClicked(){
    if(this.menuShown){
      this.visibility["infoMenu"] = false;
      this.menuShown = false;
      return;
    }
    this.menuShown = true;
    this.visibility["infoMenu"] = true;
  }
}

