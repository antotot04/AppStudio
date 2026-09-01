import { Routes } from '@angular/router';
import { AppLogin } from './app-login/app-login';
import { AppHome } from './app-home/app-home';
import { AppSignup } from './app-signup/app-signup';
import { PomodoroTimer } from './pomodoro-timer/pomodoro-timer';
import { StudyZone } from './study-zone/study-zone';
import { DeckPage } from './deck-page/deck-page';
import { StudyDeck } from './study-deck/study-deck';

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
        component: AppHome,
        children: [
            {
                path: '',
                pathMatch: 'full',
                redirectTo: 'timer'
            },
            {
                path: 'timer',
                component: PomodoroTimer
            },
            {
                path: 'study',
                component: StudyZone
            },
            {
                path: 'study/deck/:id',
                component: DeckPage
            },
            {
                path: 'study/learn/:deckId',
                component: StudyDeck
            }
        ]
    }
];
