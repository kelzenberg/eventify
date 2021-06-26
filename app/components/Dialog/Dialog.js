import React from "react";
import PropTypes from 'prop-types';
import { Modal } from "react-bootstrap";

export function Dialog(props) {
    return <Modal show={props.show} onHide={props.onHide}>
        <Modal.Header closeButton={!!props.closable}>
            <Modal.Title>{props.title}</Modal.Title>
        </Modal.Header>
        <Modal.Body>
            {props.children}
        </Modal.Body>
        <Modal.Footer>
            {props.buttons.map((b, i) => <button key={i} type="button" className={getClassNameOfButton(b)} onClick={b.onClick}>{b.text}</button>)}
        </Modal.Footer>
    </Modal>
}

Dialog.propTypes = {
    show: PropTypes.bool.isRequired,
    onHide: PropTypes.func.isRequired,
    title: PropTypes.node.isRequired,
    closable: PropTypes.bool,
    children: PropTypes.node.isRequired,
    buttons: PropTypes.arrayOf(PropTypes.object).isRequired
};

function getClassNameOfButton(b) {
    if(!!b.outline) {
        return "btn btn-outline-primary";
    } else {
        return "btn btn-primary";
    }
}

export function InfoDialog(props) {
    return <Dialog 
        show={props.show}
        onHide={props.onHide}
        closable={true}
        title={props.title}
        buttons={[{text: "OK", onClick: props.onHide}]}
    >
        {props.message == undefined ? "" : props.message}
    </Dialog>
}

InfoDialog.propTypes = {
    show: PropTypes.bool.isRequired,
    onHide: PropTypes.func.isRequired,
    title: PropTypes.node.isRequired,
    message: PropTypes.node // not required to allow null
};

export function ConfirmationDialog(props) {
    function confirm() {
        if(props.onConfirm != undefined) props.onConfirm();
        if(props.onResult != undefined) pros.onResult(true);
    }

    function deny() {
        if(props.onDeny != undefined) props.onDeny();
        if(props.onResult != undefined) pros.onResult(false);
    }

    return <Dialog 
        show={props.show}
        onHide={deny}
        closable={true}
        title={props.title}
        buttons={[{text: props.denyText, onClick: deny, outline: !props.highlightDeny}, {text: props.confirmText, onClick: confirm, outline: props.highlightDeny}]}
    >
        {props.message}
    </Dialog>
}

ConfirmationDialog.propTypes = {
    show: PropTypes.bool.isRequired,
    title: PropTypes.node.isRequired,
    message: PropTypes.node.isRequired,
    confirmText: PropTypes.string.isRequired,
    denyText: PropTypes.string.isRequired,
    highlightDeny: PropTypes.bool, // highlight deny button instead of confirm
    onConfirm: PropTypes.func,
    onDeny: PropTypes.func,
    onResult: PropTypes.func
};