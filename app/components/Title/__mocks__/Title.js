import React from "react";

export default jest.fn((props) => {return <div>Mocked Title Component: {props.children}</div>});