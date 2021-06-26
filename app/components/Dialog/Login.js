import React from 'react';
import PropTypes from 'prop-types';
import { Modal } from "react-bootstrap";
import * as api from '../../common/api';

export function LoginDialog(props) {
    const [wrongLogin, setWrongLogin] = React.useState(false);
    const [email, setEmail] = React.useState("");
    const [password, setPassword] = React.useState("");

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

    return <Modal show={props.show} onHide={props.onHide}>
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
                <input type="password" className="form-control" id="loginPassword" value={password} onChange={e => setPassword(e.target.value)} onKeyDown={event => event.keyCode == 13 ? login() : {}}/>
            </div>
            <p className="text-error" hidden={!wrongLogin}>The entered credentials were wrong.</p>
        </Modal.Body>
        <Modal.Footer>
            <button type="button" className="btn btn-primary" onClick={login}>Login</button>
        </Modal.Footer>
    </Modal>
}

LoginDialog.propTypes = {
    show: PropTypes.bool,
    onHide: PropTypes.func
}