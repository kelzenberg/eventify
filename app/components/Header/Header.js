import React from 'react';
import * as api from '../../common/api';
import * as stateKeeper from '../../common/stateKeeper';

export default function Header() {
    let userInfo = React.useContext(stateKeeper.UserContext);

    return <div className="container-fluid p-4">
        <div className="d-flex">
            <a href="/you" className="me-auto">
                <img src="/assets/logo_text.svg" className="bi me-2" height="40" aria-label="Eventify"/>
            </a>
            <div className="col-md-3 text-end">
                <span className="text-muted">Logged in as {userInfo.email}</span>
                <button className="btn btn-outline-darkGray ms-3" onClick={logout}>Logout</button>
            </div>
        </div>
    </div>
}

function logout() {
    api.logout();
    stateKeeper.clearLogin();
    window.location = "/";
}