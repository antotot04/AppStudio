export interface PomoSettingsForm {
    timer: {
        short: number,
        long: number, 
        frequency: number
    }
    sound: {
        ringtone: string,
        ringtone_volume: number,
        background: string,
        background_volume: number
    }
}
