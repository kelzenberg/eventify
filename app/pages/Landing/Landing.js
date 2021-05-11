import React from 'react';
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
                    <div className="user"/>
                </div>
            </div>
        </div>
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
    return <div>
        <div className="mb-3">
            <h2>Register Now!</h2>
        </div>
        <div className="mb-3">
            <label htmlFor="emailInput" className="form-label">Email address</label>
            <input type="email" className="form-control" id="emailInput"/>
        </div>
        <div className="mb-3">
            <label htmlFor="passwordInput" className="form-label">Password</label>
            <input type="password" className="form-control" id="passwordInput" />
        </div>
        <button type="submit" className="btn btn-primary">Register</button>
    </div>
}