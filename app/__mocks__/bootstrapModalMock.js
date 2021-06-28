import React from "react";
let reactBootstrap = jest.requireActual("react-bootstrap");

const Modal = jest.fn();
Modal.mockImplementation((props) => {
    return <reactBootstrap.Modal {...props}>{props.children}</reactBootstrap.Modal>;
});

Modal.Header = reactBootstrap.Modal.Header;
Modal.Title = reactBootstrap.Modal.Title;
Modal.Body = reactBootstrap.Modal.Body;
Modal.Footer = reactBootstrap.Modal.Footer;

export default Modal;