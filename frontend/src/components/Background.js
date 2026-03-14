import Lottie from 'lottie-react';
import animationData from '../assets/Fish.json';

export default function Background() {
    return (
        <div className="bg-lottie" aria-hidden>
            <div className="bg-lottie-item left">
                <Lottie
                    animationData={animationData}
                    loop
                    autoplay
                    // reduced width/height so animation isn't zoomed/cropped
                    style={{ width: '80vw', height: '80vh', maxWidth: 'none', transform: 'translateX(17vw) translateY(4vh)' }}
                    rendererSettings={{ preserveAspectRatio: 'xMidYMid meet' }}
                />
            </div>

            <div className="bg-lottie-item right">
                <Lottie
                    animationData={animationData}
                    loop
                    autoplay
                    // mirror and offset the right instance so both face inward and are not zoomed
                    style={{ width: '80vw', height: '80vh', maxWidth: 'none', transform: 'scaleX(-1) translateX(15vw) translateY(4vh)'} }
                    rendererSettings={{ preserveAspectRatio: 'xMidYMid meet' }}
                />
            </div>
        </div>
    );
}