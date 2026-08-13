export interface UserSettings {
    timer: {
        shortPause: number,
        longPause: number, 
        frequency: number
    },
    suono: {
        ringtone: string, // uuid
        ringtone_volume: number,
        background: string | null, // uuid
        background_volume: number
    }
}
