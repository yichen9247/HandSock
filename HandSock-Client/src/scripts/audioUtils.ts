export const playNoticeVoice = async (): Promise<void> => {
    const audio = localStorage.getItem('audio');
    const audioRef: any = document.getElementById("audioRef");

    let audioModule: any;
    switch (audio) {
        case 'apple':
            audioModule = import('@/assets/audio/apple.mp3');
            break;
        case 'momo':
            audioModule = import('@/assets/audio/momo.mp3');
            break;
        case 'huaji':
            audioModule = import('@/assets/audio/huaji.mp3');
            break;
        default:
            localStorage.setItem('audio', 'default');
            audioModule = import('@/assets/audio/default.mp3');
            break;
    }

    const audioFile = await audioModule;
    audioRef.src = audioFile.default;
    audioRef.load();
    if (!audioRef.paused) audioRef.pause();
    audioRef.play().catch((): void => {});
}