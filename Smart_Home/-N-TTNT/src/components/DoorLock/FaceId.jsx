import { useState, useRef, useEffect } from "react";
import Webcam from "react-webcam";
import { color, motion } from "framer-motion";
import apiService from "../../api/api";

const streamImageMessage = (ws, webcamRef) => {
    const imageSrc = webcamRef.current.getScreenshot();
    if (imageSrc) {
        ws?.send(JSON.stringify({ image: imageSrc }));
    }
};

const waitForScreenshot = async (webcamRef, timeout = 5000, interval = 100) => {
    const start = Date.now();
    return new Promise((resolve, reject) => {
        const check = () => {
            const screenshot = webcamRef.current?.getScreenshot();
            if (screenshot) {
                resolve(screenshot);
            } else if (Date.now() - start >= timeout) {
                reject(new Error("Timed out waiting for screenshot"));
            } else {
                setTimeout(check, interval);
            }
        };
        check();
    });
};

const WebcamCapture = ({ webcamRef }) => (
    <div className="d-flex align-items-center justify-content-center mb-4">
        <div className="position-relative">
            <Webcam
                audio={false}
                ref={webcamRef}
                width={480}
                height={360}
                screenshotFormat="image/jpeg"
                videoConstraints={{ facingMode: "user" }}
                className="rounded border border-secondary"
            />
            <motion.div
                className="position-absolute w-100"
                style={{
                    height: "6px",
                    background: "linear-gradient(to right, transparent, lime, transparent)",
                    opacity: 0.8,
                    boxShadow: "0 0 12px lime",
                }}
                animate={{ top: ["0%", "90%"] }}
                transition={{
                    duration: 2,
                    repeat: Infinity,
                    repeatType: "reverse",
                }}
            />
        </div>
    </div>
);
const registerFace = async (token, wss, webcamRef, setFacePrompt, setStreaming, setActionState) => {
    const response = await fetch('http://localhost:8070/api/face/register', {
        method: 'POST',
        headers: {
            'Authorization': `Bearer ${token}`,
        },
    })

    if (!response.ok) {
        console.error('Authorization failed:', response.statusText);
        return;
    }

    wss.current = new WebSocket(`ws://localhost:8000/client/face/register?token=${token}`);
    console.log('WebSocket server running on ws://localhost:8000');

    wss.current.onopen = () => {
        console.log('WebSocket connection opened');
        streamImageMessage(wss.current, webcamRef);
    };

    wss.current.onmessage = (event) => {

        const message = JSON.parse(event.data);
        console.log('Received message:', message);
        if (message.data.status === 0) {
            setFacePrompt({ text: 'Not match', color: 'text-danger' });
            streamImageMessage(wss.current, webcamRef);
        }
        else if (message.data.status === 1) {
            setStreaming(false);
            setFacePrompt({ text: 'Face ID recorded successfully. Try press \'Identify\' to open door.', color: 'text-success' });
        }
        else if (message.data.status === 2) {
            streamImageMessage(wss.current, webcamRef);
            setFacePrompt({ text: 'Come closer to the camera', color: 'text-danger' });
        } else if (message.data.status === 3) {
            setFacePrompt({ text: 'Move slightly away from the camera', color: 'text-danger' });
            streamImageMessage(wss.current, webcamRef);
        }
        else if (message.data.status === 4) {
            setFacePrompt({ text: 'Time out!', color: 'text-danger' });
            setStreaming(false);
        } else if (message.data.status === -1) {
            console.log('Error:', message.error);
            setStreaming(false);
        }
    };

    wss.current.onerror = (error) => {
        console.log('WebSocket error:', error);
    };

    wss.current.onclose = () => {
        console.log('WebSocket connection closed');
    };

}
const identifyFace = async (token, wss, webcamRef, setFacePrompt, setStreaming, setActionState, setDoorOpen, setShowDoorLock) => {
    const response = await fetch('http://localhost:8070/api/face/identify', {
        method: 'POST',
        headers: {
            'Authorization': `Bearer ${token}`,
        },
    })

    if (!response.ok) {
        console.error('Authorization failed:', response.statusText);
        return;
    }

    wss.current = new WebSocket(`ws://localhost:8000/client/face/identify?token=${token}`);
    console.log('WebSocket server running on ws://localhost:8000');

    wss.current.onopen = () => {
        console.log('WebSocket connection opened');
        streamImageMessage(wss.current, webcamRef);
    };

    wss.current.onmessage = async (event) => {
        const message = JSON.parse(event.data);
        console.log('Received message:', message);
        if (message.data.status === 0) {
            setFacePrompt({ text: 'Not match', color: 'text-danger' });
            streamImageMessage(wss.current, webcamRef);
        }
        else if (message.data.status === 1) {
            setStreaming(false);
            setFacePrompt({ text: 'Face ID matched!', color: 'text-success' });
            await apiService.controlDoor("open");
            setDoorOpen(true);
            setShowDoorLock(false);
        }
        else if (message.data.status === 2) {
            streamImageMessage(wss.current, webcamRef);
            setFacePrompt({ text: 'Come closer to the camera', color: 'text-danger' });
        } else if (message.data.status === 3) {
            setFacePrompt({ text: 'Move slightly away from the camera', color: 'text-danger' });
            streamImageMessage(wss.current, webcamRef);
        }
        else if (message.data.status === 4) {
            setFacePrompt({ text: 'Time out!', color: 'text-danger' });
            setStreaming(false);
        } else if (message.data.status === -1) {
            console.log('Error:', message.error);
            setStreaming(false);
        }
    };

    wss.current.onerror = (error) => {
        console.log('WebSocket error:', error);
    };

    wss.current.onclose = () => {
        console.log('WebSocket connection closed');
    };

}
export default function FaceId(props) {
    const { setDoorOpen, setShowDoorLock } = props
    const webcamRef = useRef(null);
    const wss = useRef(null);
    const [action, setActionState] = useState(0); // 0: idle, 1: identify, 2: register
    const [facePrompt, setFacePrompt] = useState({ text: "Click to identify or update your face ID", color: "text-dark" });
    const [streaming, setStreaming] = useState(false);


    const token = localStorage.getItem("accessToken");

    useEffect(() => {
        const startStreaming = async () => {
            if (streaming && webcamRef.current) {
                try {
                    await waitForScreenshot(webcamRef, 5000, 1000);
                    if (action === 1) {
                        await identifyFace(token, wss, webcamRef, setFacePrompt, setStreaming, setActionState, setDoorOpen, setShowDoorLock);
                    } else if (action === 2) {
                        await registerFace(token, wss, webcamRef, setFacePrompt, setStreaming, setActionState);
                    }
                } catch (error) {
                    console.error('Error:', error);
                }
            }
        };
        startStreaming();

        return () => {
            if (wss.current) {
                wss.current.close();
                wss.current = null;
            }
        };
    }, [streaming]);

    const handleStream = () => {
        setStreaming(!streaming);
    };

    const identifyButtonHandle = () => {
        if (action === 2) return;
        if (action === 1) {
            setFacePrompt({ text: "Click to identify or update your face ID", color: "text-dark" });
            setActionState(0);
            setStreaming(false);
        } else {
            setActionState(1);
            setStreaming(true);
        }
    };

    const updateButtonHandle = () => {
        if (action === 1) return;
        handleStream();
        if (action === 2) {
            setFacePrompt({ text: "Click to identify or update your face ID", color: "text-dark" });
            setActionState(0);
            setStreaming(false);
        } else {
            setActionState(2);
            setStreaming(true);
        }
    };

    return (
        <div className="container h-100 d-flex flex-column align-items-center justify-content-center text-center py-2 bg-white">
            <h1 className="h3 fw-bold mb-3">Face ID Authentication</h1>
            <p className={`mb-3  ${facePrompt.color}`}>{facePrompt.text}</p>

            {action === 0 && (
                <div className="mb-4">
                    <img src="src/assets/face-id-1.png" alt="Face ID Placeholder" width={128} height={128} />
                </div>
            )}

            {(action === 1 || action === 2) && (
                <WebcamCapture webcamRef={webcamRef} />
            )}

            <div className="d-flex gap-3">
                <button className="btn btn-primary px-4" onClick={identifyButtonHandle}>
                    Identify
                </button>
                <button className="btn btn-primary px-4" onClick={updateButtonHandle}>
                    Update
                </button>
            </div>
        </div>
    );
}
