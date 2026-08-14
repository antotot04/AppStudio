export interface PomoSettingsForm {
    timer: {
        shortPause: number,
        longPause: number, 
        frequency: number
    },
    sound: {
        ringtone: string, // uuid
        ringtone_volume: number,
        background: string, // uuid
        background_volume: number
    }
}
