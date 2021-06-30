import React from "react";

export const Dialog = jest.fn((props) => <div>Mock Dialog: {props.children}</div>);
export const InfoDialog = jest.fn(() => null);
export const ConfirmationDialog = jest.fn(() => null);