import React from 'react';
import PropTypes from 'prop-types';
import update from 'immutability-helper';
import { Accordion, AccordionContext, useAccordionButton } from 'react-bootstrap';
import { InfoDialog } from '../Dialog/Dialog';
import { BalanceModal } from './BalanceSheet';
import * as stateKeeper from '../../common/stateKeeper';
import * as api from '../../common/api';

export default function ExpenseSharingModule(props) {
    const [moduleData, setModuleData] = React.useState(props.moduleData);
    const [editorOpen, setEditorOpen] = React.useState(false);
    const [errorMessage, setErrorMessage] = React.useState(null);
    const [balanceVisible, setBalanceVisible] = React.useState(false);

    React.useEffect(() => {
        if(props.moduleData.payments == null) return;
        if(props.moduleData.id != moduleData.id) {
            setModuleData(props.moduleData);
            setEditorOpen(false);
            setErrorMessage(null);
        }
        if(props.moduleData.payments.length > moduleData.payments.length) {
            setModuleData(props.moduleData);
        }
    }, [props.moduleData]);

    function savePayment(paymentData) {
        api.addPaymentToExpenseSharing(moduleData.id, paymentData)
        .then((result) => {
            if(moduleData.payments === null) {
                setModuleData(update(moduleData, {payments: {$set: [result]}}));
            } else {
                setModuleData(update(moduleData, {payments: {$push: [result]}}));
            }
            setEditorOpen(false);
        })
        .catch(err => {
            console.warn(err);
            setErrorMessage("Unfortunately an error prevented us from saving the expense. Please try again.");
        });
    }

    function deletePayment(paymentID) {
        api.deletePaymentFromExpenseSharing(moduleData.id, paymentID)
        .then((result) => {
            let newPayments = moduleData.payments.filter(p => p.id != paymentID);
            setModuleData(update(moduleData, {payments: {$set: newPayments}}));
        })
        .catch(err => {
            console.warn(err);
            setErrorMessage("Unfortunetely an error precented us from deleting the payment. Please try again.");
        });
    }

    return <>
        <div className="d-flex flex-row justify-content-between">
            <p className="my-2">{moduleData.description}</p>
            <a role="button" className="mx-3 link-primary fw-bold" onClick={() => setBalanceVisible(true)}>Create Balance</a>
            {/* <button className="btn btn-outline-primary">Create Balance</button> */}
        </div>
        <Accordion defaultActiveKey="0" flush className="container-fluid gx-1 my-2">
            {moduleData.payments === null ? null : 
                moduleData.payments.sort(paymentSortFunc).map(payment => <Payment payment={payment} key={payment.id} onDelete={deletePayment}/>)
            }
        </Accordion>
        {!editorOpen ? null :
            <PaymentEditor moduleID={moduleData.id} members={props.event.users} onAddPayment={savePayment} onCancelEdit={() => setEditorOpen(false)}/>
        }
        <div hidden={editorOpen} onClick={() => setEditorOpen(true)} className="rounded border-dashed border-gray mt-4 w-100 p-3 text-center text-primary" role="button" aria-label="Add new Item">
            <img src="/assets/icons/add.svg" alt="" className="pe-2" style={{verticalAlign: "sub"}}/>
            ADD NEW EXPENSE
        </div>
        <InfoDialog
            show={errorMessage !== null}
            onHide={() => setErrorMessage(null)}
            title="Expense Sharing Module"
            message={errorMessage}
        />
        <BalanceModal
            show={balanceVisible}
            onHide={() => setBalanceVisible(false)}
            moduleData={moduleData}
            refreshEvent={() => {
                props.refreshEvent()
            }}
        />
    </>
}

ExpenseSharingModule.icon = "finances.svg";

ExpenseSharingModule.propTypes = {
    event: PropTypes.object,
    moduleData: PropTypes.object
}

function Payment({payment, onDelete}) {
    let userInfo = React.useContext(stateKeeper.UserContext);

    const months = ["Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Okt", "Nov", "Dez"];
    let date = new Date(payment.createdAt);

    let payer = "You";
    let balance = 0;
    if(payment.payer.id != userInfo.id) {
        payer = payment.payer.displayName;
    } else {
        balance += payment.amount;
    }

    // NOTE: shares received from the server always contain the absolute amounts no matter what the share type is

    // calculate your balance for this payment
    balance -= payment.shares
    .filter(s => s.shareHolder.id == userInfo.id)
    .map(share => share.amount)
    .reduce((sum, v) => sum+v, 0); // initial value for reduce is required for empty arrays

    // let effectiveShare = -1.23;
    if(balance > 0) {
        var icon = "↗️";
        var textStyle = "text-primary";
    } else if(balance < 0) {
        var icon = "↘️";
        var textStyle = "text-secondary";
    } else {
        var icon = "➡️";
        var textStyle = "text-muted";
    }

    const showDetails = useAccordionButton(payment.id, () => {});
    const { activeEventKey: detailedPaymentID } = React.useContext(AccordionContext);

    return <div>
        <div onClick={showDetails} className="row hover-accent p-2 gx-0 cursor-default">
            <div className="col-1 text-primary text-center fw-slightly-bold">
                <div>{date.getDate()}</div>
                <div>{months[date.getMonth()]}</div>
            </div>
            <div className="col">
                <div className="fw-slightly-bold" id={payment.id + "_name"}>{payment.title}</div>
                <div>{payer} paid {niceFloat(payment.amount)} € for {payment.shares.length} {payment.shares.length == 1 ? "Person" : "People"}</div>
            </div>
            <div className="col-2 d-flex align-items-center justify-content-end">
                <img src="/assets/icons/trash.png" onClick={() => onDelete(payment.id)} hidden={payment.payer.id != userInfo.id || detailedPaymentID != payment.id} role="button" aria-label="Delete this payment"/>
            </div>
            <div className="col-2 text-end">
                <div>{icon}</div>
                <div className={`fw-slightly-bold ${textStyle}`}>{niceFloat(balance)} €</div>
            </div>
        </div>
        <Accordion.Collapse eventKey={payment.id}>
            <div className="container-fluid gx-3 border-bottom">
                <span className="fw-slightly-bold">{{
                    "DECIMAL": "The money was divided up individually.",
                    "PERCENTAGE": "The money was divided on a percentage basis.",
                    "EQUAL": "The money was devided equaly."
                }[payment.shareType]}</span>
                {payment.shares.map(share => <div className="row p-1" key={share.id}>
                    <div className="col">
                        {share.shareHolder.displayName}
                    </div>
                    <div className="col-2">
                        {roundToDecimalPlaces((share.amount / payment.amount) * 100, 2)} %
                    </div>
                    <div className="col-2">
                        {share.amount} €
                    </div>
                </div>)}
            </div>
        </Accordion.Collapse>
    </div>
}

// An editor for creating new shares or editing existing ones.
function PaymentEditor(props) {
    const userInfo = React.useContext(stateKeeper.UserContext);
    const [title, setTitle] = React.useState("");
    const [amount, setAmount] = React.useState(0);
    const [payer, setPayer] = React.useState(userInfo);
    const [shareType, setShareType] = React.useState("DECIMAL");
    const [shares, setShares] = React.useState([]);
    /* Shares is an array of objects with the following structure:
        {
            user: {
                id: "",         // id of the user
                displayName: "" // name of the user
            },
            roundingPriority: 0.0, // random value that is used when we can't split a value equally
            percent: 0.5,       // percent [0, 1] that the user has entered manually
            percentInput: "50", // Value of the percent input field. Can be the input of the user or a calculated value if percent is null.
            amount: null,       // amount that the user has entered manually
            amountInput: "10"   // Value of the amount input field. Can be the input of the user or a calculated value if amount is null.
        }

        If the underlying value ('percent' or 'amount') is null the user has not entered something in the corresponding text field
        and the program should calculate the value using an equal split.
        The calculated value will also be stored in the corresponding "...Input" value.
        Only percent or amount can not be null at any time depending on the used shareType.
        If that is "DECIMAL" only 'amount' can be not null and if it is "PERCENTAGE" only 'percent' can.
        Read more about the calculation of all values in the 'calculateShares' function.
    */

    function changeAmount(e) {
        let value = e.target.value;
        if(value == "") value = "0";
        if(value.length > 1 && value[0] == "0" && parseFloat(value) > 1) value = value.slice(1);

        setAmount(value);
        setShares(calculateShares(shares, value));
    }

    function changePayer(e) {
        setPayer(props.members.find(m => m.id == e.target.value));
    }

    function changeShareType(newType) {
        if(newType == shareType) return;
        // change all shares that have a set percent or amount value (which one depends on the current shareType) to the new shareType
        // If we change from "DECIMAL" to "PERCENTAGE" we will convert all amounts to percents
        // and if we change from "PERCENTAGE" to "DECIMAL" we do the opposite.
        setShares(calculateShares(shares.map(share => {
            let newShare = share;
            if(share.percent !== null && shareType == "PERCENTAGE") {
                newShare = update(share, {
                    amount: {$set: amount * share.percent},
                    percent: {$set: null}
                })
            }
            if(share.amount !== null && shareType == "DECIMAL") {
                newShare = update(share, {
                    percent: {$set: share.amount / amount},
                    amount: {$set: null}
                })
            }
            return newShare;
        }), amount));
        setShareType(newType);
    }

    function changeShares(newShares) {
        setShares(calculateShares(newShares, amount));
    }

    function savePayment() {
        let amountFunc = null;
        if(shareType == "DECIMAL")
            amountFunc = share => share.amountInput;
        else if(shareType == "PERCENTAGE")
            amountFunc = share => share.percentInput;
        props.onAddPayment({
            title: title,
            amount: parseFloat(amount),
            userId: payer.id,
            shareType: shareType,
            shares: shares.map(share => {return {
                amount: parseFloat(amountFunc(share)), // percentages will be between [0, 100]
                userId: share.user.id
            }})
        });
    }

    // Members that have not already been paid for in this payment.
    let uninvolvedMembers = props.members.filter(m => 
        shares.find(s =>
            s.user.id == m.id
        ) === undefined // not a person being paid for
    );

    let amountIsValid = !isNaN(parseFloat(amount)) && parseFloat(amount) != 0;
    let sharesAreValid = validateShares(shares, shareType, amount);
    let allValid = title != "" && sharesAreValid && shares.length > 0 && amountIsValid;

    return <div className="border rounded m-1 p-3">
        <form>
            {/* ====== Title ====== */}
            <div className="row mb-3">
                <label htmlFor={`${props.moduleID}_editor_title`} className="col-md-3 col-form-label">Name</label>
                <div className="col align-items-center">
                    <input type="text" id={`${props.moduleID}_editor_title`} placeholder="Name of payment"
                        className={`form-control ${title == "" ? "is-invalid" : ""}`}
                        value={title} onChange={e => setTitle(e.target.value)}
                    />
                </div>
            </div>
            {/* ====== Amount ====== */}
            <div className="row mb-3">
                <label htmlFor={`${props.moduleID}_editor_amount`} className="col-md-3 col-form-label">Amount</label>
                <div className="col d-flex align-items-center">
                    <input type="number" id={`${props.moduleID}_editor_amount`}
                        className={`form-control flex-grow-1 ${amountIsValid ? "" : "is-invalid"}`}
                        value={amount} onChange={e => changeAmount(e)}
                    />
                    <span className="mx-2">€</span>
                </div>
            </div>
            {/* ====== Paid By ====== */}
            <div className="row mb-3">
                <label htmlFor={`${props.moduleID}_editor_paidBy`} className="col-md-3 col-form-label">Paid By</label>
                <div className="col">
                    <select className="form-select" value={payer.id} onChange={changePayer} aria-label="Select member that the money has been paid by.">
                        {props.members.map(p => <option key={p.id} value={p.id}>{p.displayName}</option>)}
                    </select>
                </div>
            </div>
            {/* ====== Split ====== */}
            <div className="row mb-3">
                <label className="col-md-3 col-form-label">Split</label>
                <div className="col btn-group" role="group" aria-label="How should the money be split?">
                    <input
                        checked={shareType == "PERCENTAGE"}
                        onChange={() => changeShareType("PERCENTAGE")}
                        type="radio" className="btn-check"
                        name={`${props.moduleID}_editor_shareType`} id={`${props.moduleID}_editor_shareType_percent`}
                    />
                    <label className="btn btn-outline-primary" htmlFor={`${props.moduleID}_editor_shareType_percent`}>Relative</label>
                    <input
                        checked={shareType == "DECIMAL"}
                        onChange={() => changeShareType("DECIMAL")}
                        type="radio" className="btn-check"
                        name={`${props.moduleID}_editor_shareType`} id={`${props.moduleID}_editor_shareType_fixed`}
                    />
                    <label className="btn btn-outline-primary" htmlFor={`${props.moduleID}_editor_shareType_fixed`}>Fixed</label>
                </div>
            </div>
            {/* ====== Shares ====== */}
            <div className="row mb-3">
                <label className="col-md-3 col-form-label">Paid for</label>
                <div className="col">
                    <ShareList 
                        shareType={shareType}
                        shares={shares}
                        amount={amount}
                        members={uninvolvedMembers}
                        onSetShares={changeShares}
                        isValid={sharesAreValid}
                    />
                </div>
            </div>
            {/* ====== Save ====== */}
            <div className="row gy-3 gy-md-0">
                <div className="col-12 col-sm-4 col-xl-2 d-grid">
                    <button type="button" className="btn btn-outline-primary" onClick={props.onCancelEdit}>Cancel</button>
                </div>
                <div className="col-12 col-sm-8 col-xl-10 d-grid">
                    <button type="button" className="btn btn-primary" onClick={savePayment} disabled={!allValid}>Save Expense</button>
                </div>
            </div>
        </form>
    </div>
}

// Lists all shares and offers the ability to add and edit them.
function ShareList(props) {
    function changeNewPerson(e) {
        let person = props.members.find(m => m.id == e.target.value);
        props.onSetShares(props.shares.concat([{
            user: person,
            roundingPriority: Math.random(),
            percent: null,
            percentInput: "",
            amount: null,
            amountInput: ""
        }]));
    }

    function removeShare(i) {
        props.onSetShares(update(props.shares, {$splice: [[i]]}));
    }

    function changePercent(shareIndex, rawValue) {
        let value = null;
        if(rawValue !== null && rawValue != "")
            value = parseFloat(rawValue) / 100;
        let newShares = update(props.shares, {[shareIndex]: {
            percent: {$set: value},
            percentInput: {$set: rawValue}
        }});
        props.onSetShares(newShares);
    }

    function changeAmount(shareIndex, rawValue) {
        let value = null;
        if(rawValue !== null && rawValue != "")
            value = parseFloat(rawValue);
        let newShares = update(props.shares, {[shareIndex]: {
            amount: {$set: value},
            amountInput: {$set: rawValue}
        }});
        props.onSetShares(newShares);
    }

    return <ul className="list-group">
        {props.shares.map((share, i) =>
            <li key={share.user.id} className="list-group-item d-flex justify-content-between align-items-center">
                <button type="button" className="btn-close me-2" onClick={() => removeShare(i)} aria-label="Remove Person" />
                <div className="fw-bold flex-grow-1">{share.user.displayName}</div>
                <div className="mx-2 d-flex align-items-center pe-3 border-end">
                    <span hidden={props.shareType == "PERCENTAGE"}>{share.percentInput}</span>
                    <NullableInput
                        hidden={props.shareType != "PERCENTAGE"}
                        className="flex-grow-1 me-2"
                        type="number"
                        value={share.percentInput}
                        isNull={share.percent === null}
                        isValid={props.isValid}
                        onChange={e => changePercent(i, e.target.value)}
                        min={0}
                        max={100}
                    />
                    <span>%</span>
                </div>
                <div className="mx-2 d-flex align-items-center">
                    <span hidden={props.shareType == "DECIMAL"}>{share.amountInput}</span>
                    <NullableInput
                        hidden={props.shareType != "DECIMAL"}
                        className="flex-grow-1 me-2"
                        type="number"
                        value={share.amountInput}
                        isNull={share.amount === null}
                        isValid={props.isValid}
                        onChange={e => changeAmount(i, e.target.value)}
                        min={0}
                        max={props.amount}
                    />
                    <span>€</span>
                </div>
            </li>
        )}
        <li className="list-group-item d-flex align-items-center">
            <div className="flex-grow-1">
                <select
                    className="form-select"
                    value=""
                    onChange={changeNewPerson}
                    disabled={props.members.length == 0}
                    aria-label="Select member that the money has been paid for."
                >
                    <option value="" disabled={true} key="...">Add Person...</option>
                    {props.members.map(p => <option value={p.id} key={p.id}>{p.displayName}</option>)}
                </select>
            </div>
        </li>
    </ul>
}

function NullableInput(props) {
    let inputProps = {value: ""};
    if(props.isNull)
        inputProps.placeholder = props.value;
    else
        inputProps.value = props.value;
    return <div className={`btn-group align-items-center ${props.className}`} hidden={props.hidden}>
        <input type={props.type} {...inputProps} onChange={props.onChange} className={`form-control ${props.isNull ? "passive" : ""} ${props.isValid ? "" : "is-invalid"}`} />
        <button type="button" hidden={props.isNull} className="btn-close me-4 position-absolute end-0" onClick={() => props.onChange({target:{value: null}})} aria-label="Remove Person"/>
    </div>
}

// calculateShares takes an array of shares and populates 'percentInput' and 'amountInput' with calculated values
// if 'percent' or 'amount' respectively are null. If one is not null that will be taken into account when calculating the values.
// It will operate on a copy of the original array and also sort it by the user name in alphabetical order.
function calculateShares(originalShares, amountSum) {
    if(amountSum == undefined) console.warn("called calculateShares without amount");
    amountSum = parseFloat(amountSum);
    let newShares = [];
    let unaccountedPercent = 1;
    let unaccountedAmount = amountSum;
    let unspecifiedShareCount = 0;

    // First we will complete shares that have either percent or amount values set. (Only one can be set at once.)
    // We will also collect some information about the data.
    for(let share of originalShares) {
        let newShare = null;
        let percent = 0;
        let amount = 0;
        if(share.percent !== null) { // user has set 'percent', calculate 'amount'
            amount = amountSum * share.percent;
            if(amount < 0) amount = 0;
            newShare = update(share, {amountInput: {$set: niceFloat(amount)}});
            percent = newShare.percent;
        } else if(share.amount !== null) { // user has set 'amount', calculate 'percent'
            percent = roundToDecimalPlaces(share.amount/amountSum, 4);
            if(percent < 0) percent = 0;
            newShare = update(share, {percentInput: {$set: niceFloat(percent*100)}});
            amount = newShare.amount;
        } else {
            unspecifiedShareCount++;
        }

        if(newShare !== null) {
            unaccountedAmount -= amount;
            unaccountedPercent -= percent;
            newShares.push(newShare);
        }
    }

    if(unspecifiedShareCount == 0) {
        return newShares;
    }

    let roundingError = 0;

    // Now we can fill shares that have neither percent nor amount set.
    for(let share of originalShares) {
        if(share.percent === null && share.amount === null) {
            let percent = roundToDecimalPlaces((unaccountedPercent / unspecifiedShareCount) * 100, 2);
            let amount = unaccountedAmount / unspecifiedShareCount;
            if(percent < 0) percent = 0;
            if(amount < 0) amount = 0;
            // round down amount and store lost cents.
            let re;
            [amount, re] = splitRealMoney(amount);
            roundingError += re;

            newShares.push(update(share, {
                percentInput: {$set: niceFloat(percent)},
                amountInput: {$set: niceFloat(amount)},
            }))
        }
    }

    // Now we might have accumulated some rounding error and we will need to add these cents to some people.
    // Specifically people for wich the user hasn't already set a specific amount of money.
    // We will use the shares roundingPriority value as a random (but static) way to determine who should need to pay the additional cents.
    // Note: roundingError should be a multiple of 1 (with the usual inaccuracies of floating point numbers)
    roundingError = Math.round(roundingError);
    let sharesForRounding = newShares.filter(share => share.amount === null).sort((a, b) => a.roundingPriority > b.roundingPriority);
    if(roundingError > sharesForRounding.length) throw("Should not happen");
    for(let i = 0; i < roundingError; i++) {
        sharesForRounding[i].amountInput = roundToDecimalPlaces(sharesForRounding[i].amountInput + 0.01, 2);
    }

    return newShares.sort((a, b) => a.user.displayName > b.user.displayName);
}

// Validate shares return true if the current share split is valid.
function validateShares(shares, shareType, amount) {
    if(shares.length == 0) return false;
    
    let sums = shares
    .map(share => {return {
        percent: share.percent !== null ? share.percent : parseFloat(share.percentInput)/100,
        amount: share.amount !== null ? share.amount : parseFloat(share.amountInput)
    }})
    .reduce((acc, share) => {return {
        percent: acc.percent + share.percent,
        amount: acc.amount + share.amount
    }});

    // I think just checking the amount should be enough as a validation.
    // if(shareType == "DECIMAL") {
        return floatsEqual(sums.amount, amount);
    // } else if(shareType == "PERCENTAGE") {
    //     return sums.percent == 1;
    // }
}

// rounds the value to the given amount of decimal places
function roundToDecimalPlaces(value, places) {
    let offset = Math.pow(10, places);
    return Math.round(value * offset) / offset;
}

// Makes a float nicer by removing the small inaccuracies that floating point numbers often have
function niceFloat(value) {
    return roundToDecimalPlaces(value, 10);
}

// Returns true of the two floating point numbers are equal (enough).
function floatsEqual(a, b) {
    return Math.abs(a - b) < 0.0000000001;
}

function splitRealMoney(value) {
    value = (value * 100);
    return [
        Math.floor(value) / 100,
        value - Math.floor(value)
    ];
}

function paymentSortFunc(a, b) {
    return (new Date(a.createdAt).getTime()) > (new Date(b.createdAt)).getTime();
}