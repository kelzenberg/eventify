import React from 'react';
import { Modal } from 'react-bootstrap';
import * as stateKeeper from '../../common/stateKeeper';
import * as api from '../../common/api';
import "./Landing.scss";

export default function LandingPage() {
    return <>
        <div className="fancy-background"/>
        <div className="container-fluid vh-100 d-flex flex-column">
            <div className="row p-4">
                <div className="col">
                    <img src="/assets/logo_text.svg" alt="" className="logo"/>
                </div>
            </div>
            <div className="row flex-grow-1 pt-5">
                <div className="col">
                    <Center/>
                </div>
            </div>
            <div className="row p-5">
                <div className="col d-flex justify-content-end">
                    <UserLogin/>
                </div>
            </div>
        </div>
    </>
}

function UserLogin() {
    const [showLogin, setShowLogin] = React.useState(false);
    const [wrongLogin, setWrongLogin] = React.useState(false);
    const [email, setEmail] = React.useState("");
    const [password, setPassword] = React.useState("");

    const tryEnterUserSpace = () => {
        if(stateKeeper.isAuthenticated()) {
            window.location = "/you";
        } else {
            setShowLogin(true);
        }
    };

    const login = () => {
        api.authenticate(email, password)
        .then(() => {
            window.location = "/you";
        })
        .catch(err => {
            console.warn(err);
            setWrongLogin(true);
        })
    };

    return <>
        <div className="user" onClick={tryEnterUserSpace}/>
        <Modal show={showLogin} onHide={() => setShowLogin(false)}>
            <Modal.Header closeButton>
                <Modal.Title>Login</Modal.Title>
            </Modal.Header>
            <Modal.Body>
                <div className="mb-3">
                    <label htmlFor="loginEmail" className="form-label">Email address</label>
                    <input type="email" className="form-control" id="loginEmail" value={email} onChange={e => setEmail(e.target.value)}/>
                </div>
                <div className="mb-3">
                    <label htmlFor="loginPassword" className="form-label">Password</label>
                    <input type="password" className="form-control" id="loginPassword" value={password} onChange={e => setPassword(e.target.value)}/>
                </div>
                <p className="text-error" hidden={!wrongLogin}>The entered credentials were wrong.</p>
            </Modal.Body>
            <Modal.Footer>
                <button type="button" className="btn btn-primary" onClick={login}>Login</button>
            </Modal.Footer>
        </Modal>
    </>
}

function Center() {
    return <div className="container-xl">
        <div className="row">
            <div className="col">
                <h1>Event Planning Simplified.</h1>
            </div>
        </div>
        <div className="row pb-3">
            <div className="col">
                Plan your next <b>event</b> with ease. Create lists, balance the books, enjoy your event. <br/> All in one web app.
            </div>
        </div>
        <div className="row gx-5">
            {/* When both columns are next to each other the image will be on the right (last). It will be the first if the screen is too small. */}
            <div className="col order-md-last p-4">
                <img src="/assets/screenshot.png" alt=""  style={{width: "100%"}}/>
            </div>
            <div className="col-12 col-md-6 pt-3 pb-3">
                <Register/>
            </div>
        </div>
    </div>
}

function Register() {
    const [email, setEmail] = React.useState("");
    const [password, setPassword] = React.useState("");
    const [loading, setLoading] = React.useState(false);
    const [errorMessage, setErrorMessage] = React.useState("");

    const register = () => {
        if(email == "" || password == "") return;
        setLoading(true);
        let displayName = email;
        if(email.split("@").length != 0) displayName = email.split("@")[0];
        api.register(email, password, displayName)
        .then(() => {
            window.location = "/you";
        })
        .catch((err) => {
            console.warn(err);
            setLoading(false);
            setErrorMessage("Unfortunately an error occurred and the registration could not be completed. Please try again.");
        })
    };

    return <div>
        <div className="mb-3">
            <h2>Register Now!</h2>
        </div>
        <div className="mb-3">
            <label htmlFor="emailInput" className="form-label">Email address</label>
            <input type="email" className="form-control" id="emailInput" value={email} onChange={e => setEmail(e.target.value)}/>
        </div>
        <div className="mb-3">
            <label htmlFor="passwordInput" className="form-label">Password</label>
            <input type="password" className="form-control" id="passwordInput" value={password} onChange={e => setPassword(e.target.value)}/>
        </div>
        <button type="submit" className="btn btn-primary" hidden={loading} onClick={register}>Register</button>
        <button type="submit" className="btn btn-primary" disabled hidden={!loading}>
            <span className="spinner-border spinner-border-sm" role="status" aria-hidden="true"></span>
            <span className="visually-hidden">Loading...</span>
        </button>
        <p className="fs-5 fw-bold mt-1">{errorMessage}</p>
    </div>
}
