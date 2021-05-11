import React from 'react';

export default function Header() {
    return <div className="container-fluid p-4">
        <div className="d-flex">
            <a href="/you" className="me-auto">
                <img src="/assets/logo_text.svg" className="bi me-2" height="40" aria-label="Eventify"/>
            </a>
            <div className="col-md-3 text-end">
                <span className="text-muted">Logged in as todo@todo.todo</span>
                <button className="btn btn-outline-darkGray ms-3">Logout</button>
            </div>
        </div>
    </div>
}