import React from 'react';
import { LoginDialog } from '../../components/Dialog/Login';
import * as api from '../../common/api';

export default function RegisterPage() {
    const [success, setSucess] = React.useState(null);
    const [showLogin, setShowLogin] = React.useState(false);

    React.useEffect(() => {
        let params = new URLSearchParams(location.search);
        let token = params.get("verify");
        if(token === undefined) {
            redirectToHomepage();
            return;
        }
        api.completeRegistration(token)
        .then(() => {
            setSucess(true);
        })
        .catch((err) => {
            console.warn(err);
            setSucess(false);
        })
    }, []);

    function redirectToHomepage() {
        window.location = "/";
    }

    return <>
        <div className="fancy-background"/>
        <div className="container-fluid vh-100 d-flex flex-column">
            <div className="row p-4">
                <div className="col">
                    <a href="/you" className="me-auto">
                        <img src="/assets/logo_text.svg" alt="" className="logo"/>
                    </a>
                </div>
            </div>
            <div className="row flex-grow-1 pt-5">
                <div className="col">
                    <div className="container">
                        <h1>Welcome!</h1>
                        <div hidden={success !== null} >
                            <div className="d-flex align-items-center">
                                <div className="spinner-border" role="status" />
                                <span className="fw-bold ms-2">We are completing your registration...</span>
                            </div>
                        </div>
                        <div hidden={success !== true}>
                            <p className="fw-bold ms-2">Your account registration is complete!</p>
                            <button className="btn btn-primary" role="button" onClick={() => setShowLogin(true)}>Login</button>
                        </div>
                        <div hidden={success !== false}>
                            <p className="fw-bold ms-2">Unfortunately we where unable to complete the registration.<br/>
                            Please try again later if you think your verification link should still be valid.</p>
                            <button className="btn btn-primary" role="button" onClick={redirectToHomepage}>Return to Homepage</button>
                        </div>
                    </div>
                </div>
            </div>
        </div>
        <LoginDialog show={showLogin} onHide={() => setShowLogin(false)}/>
    </>
}
