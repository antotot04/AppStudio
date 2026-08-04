import { Routes } from '@angular/router';
import { AppLogin } from './app-login/app-login';
import { AppHome } from './app-home/app-home';
import { AppSignup } from './app-signup/app-signup';

export const routes: Routes = [
    {
        path: '',
        pathMatch: 'full',
        redirectTo: 'login'
    },
    {
        path: 'login',
        title: 'Log In Pomo',
        component: AppLogin
    },
    {
        path: 'signup',
        title: 'Sign Up Pomo',
        component: AppSignup
    },
    {
        path: ':username',
        component: AppHome
    }
];
