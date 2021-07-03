import React from 'react';
import PropTypes, { func } from 'prop-types';
import update from 'immutability-helper';
import { Modal } from 'react-bootstrap';
import * as api from '../../common/api';

export function BalanceModal(props) {
    return <Modal
        size="md"
        show={props.show}
        onHide={props.onHide}
        aria-labelledby="example-modal-sizes-title-lg"
    >
        <Modal.Header closeButton>
        <Modal.Title id="example-modal-sizes-title-lg">
            Balance Sheet for "{props.moduleData.title}"
        </Modal.Title>
        </Modal.Header>
        <Modal.Body>
            <BalanceSheet {...props}/>
        </Modal.Body>
        <Modal.Footer>
            <button className="btn btn-primary" onClick={props.onHide}>Close</button>
        </Modal.Footer>
    </Modal>;
};

function BalanceSheet(props) {
    const [checkedPayments, setCheckedPayments] = React.useState([]);
    const [balancePayments, setBalancePayments] = React.useState([]);

    React.useEffect(() => {
        // console.log("calculating payments", props.moduleData.payments);
        let newPayments = calculateBalancePayments(props.moduleData.payments);
        // console.log("new result", newPayments);
        setBalancePayments(newPayments);
    }, [props.moduleData.payments]);

    function checkPayment(id, check) {
        if(check) {
            setCheckedPayments(update(checkedPayments, {$push: [id]}));
        } else {
            setCheckedPayments(checkedPayments.filter(p => p != id));
        }
    }

    function pay() {
        let payments = checkedPayments.map(id => balancePayments.find(p => p.id == id));
        console.log(payments);
        let apiPromises = payments.map(p => api.addPaymentToExpenseSharing(props.moduleData.id, {
                "title":"Balance",
                "amount": p.amount,
                "userId": p.from.id,
                "shareType":"DECIMAL",
                "shares":[
                    {"amount":p.amount,"userId":p.to.id},
                ]
            })
        );
        console.log(apiPromises);
        Promise.all(apiPromises)
        .then(() => {
            window.setTimeout(() => {
                props.refreshEvent();
            }, 500);
        }).catch(err => {

        }).finally(() => {
            props.onHide();
        });
    }

    return <>
        {balancePayments.map((payment, i) => <BalancePayment
            checked={checkedPayments.includes(payment.id)}
            onCheck={checkPayment}
            payment={payment}
            key={i}
        />)}
        <div className="d-grid gap-2 d-md-flex justify-content-md-start mt-3">
            <button className="btn btn-outline-secondary" disabled={checkedPayments.length == 0} onClick={pay}>Mark as Paid</button>
        </div>
    </>;
};

function BalancePayment(props) {
    return <div>
        <div className="row p-2 gx-0 hover-accent cursor-default" onClick={() => props.onCheck(props.payment.id, !props.checked)}>
            <div className="col">
                <span className="fw-slightly-bold me-2">{props.payment.from.displayName}</span>
                pays
            </div>
            <div className="col text-end">
                <span className="fw-slightly-bold me-2">{props.payment.to.displayName}</span>
                {roundToDecimalPlaces(props.payment.amount, 2)}€
            </div>
            <div className="col-1 text-center">
                <input className="form-check-input" type="checkbox" checked={props.checked} onChange={() => {}}/>
            </div>
        </div>
    </div>
}

function calculateBalancePayments(payments) {
    // calculate dept of all people
    let people = calculateDept(payments);

    let solver = new Solver(people);
    let bestPayments = solver.run(2000);
    // console.log("RESULT", bestPayments);

    bestPayments = bestPayments.filter(p => !floatsEqual(p.amount, 0));
    bestPayments.forEach(p => {
        p.checked = false;
        p.id = p.from.id + "_" + p.to.id;
    });
    return bestPayments;

    // Example
    // [{
    //     id: "2473423479462789346",
    //     from: {
    //         id: "123",
    //         displayName: "User 1",
    //     },
    //     to: {
    //         id: "123",
    //         displayName: "User 2"
    //     },
    //     amount: 123.45,
    //     checked: false
    // }]
}

// takes a list of payments and calculates the dept of each user.
// Positive dept indicates that the user has to pay more then they receive.
// Users that come out with zero dept will be omitted.
function calculateDept(payments) {
    let people = new Map();

    function addDeptTo(user, amount) {
        if(people.has(user.id)) {
            let person = people.get(user.id);
            person.dept += amount;
            people.set(user.id, person);
        } else
            people.set(user.id, update(user, {dept: {$set: amount}}));
    }

    for(let payment of payments) {
        addDeptTo(payment.payer, -payment.amount);
        for(let share of payment.shares) {
            addDeptTo(share.shareHolder, share.amount);
        }
    }

    let list = [];
    for(let person of people.values()) {
        if(!floatsEqual(person.dept, 0)) {
            list.push(person);
        }
    }

    return list;
    // { Bring us the girl and wipe away the dept! }
}

function floatsEqual(a, b) {
    return Math.abs(a - b) < 0.0000000001;
}

// Solve calculates the best balancing payments. It should be noted that it runs in O(n!) time.
class Solver {
    constructor(people) {
        this.allPeople = people;
        this.timeout = null;
        this.abort = false;
        this.foundPaths = new Set();
        this.bestAmount = Infinity;
        this.bestPayments = [];
    }

    pathFound(path) {
        // reverse paths will lead to the same payments
        // so we store a hash of the reverse path to check if we have already checked that
        let reverseHash = this.hashReversePath(path);
        if(this.foundPaths.has(reverseHash))
            return; // already checked this path in reverse
        this.foundPaths.add(reverseHash);

        // calculate payments...
        let payments = this.calculatePayments(path);
        // ... sum them up ...
        let sumOfTransactions = payments.map(p => p.amount).reduce((acc, amount) => acc + amount, 0);
        // ... and check if they are better
        if(sumOfTransactions < this.bestAmount) {
            this.bestPayments = payments;
        }
    }

    hashReversePath(path) {
        return reverseCopy(path).join("_");
    }

    run(timeout) {
        let timeoutID = setTimeout(() => {
            console.info("timeout reached for solving balancing");
            this.abort = true;
        }, timeout);
        // this.recursiveOperation([], this.allPeople.map((p, i) => i));
        this.permute(this.allPeople.map((p, i) => i), []);
        clearTimeout(timeoutID);
        return this.bestPayments;
    }

    // adapted from: https://codereview.stackexchange.com/questions/7001/generating-all-combinations-of-an-array
    recursiveOperation(active, rest) {
        if (active.length == 0 && rest.length == 0) {
            return;
        } else if (rest.length == 0) {
            this.pathFound(active);
            if(this.abort) return;
        } else {
            this.recursiveOperation(active.concat([rest[0]]), rest.slice(1));
            this.recursiveOperation(active, rest.slice(1));
        }
    }

    // adapted from: https://stackoverflow.com/questions/9960908/permutations-in-javascript
    permute(people, usedChars) {
        var i, ch;
        for (i = 0; i < people.length; i++) {
            ch = people.splice(i, 1)[0];
            usedChars.push(ch);
            if (people.length == 0) {
                this.pathFound(usedChars);
                if(this.abort) return;
            }
            this.permute(people, usedChars);
            people.splice(i, 0, ch);
            usedChars.pop();
        }
    };

    calculatePayments(path) {
        let peoplePath = path.map(i => this.allPeople[i]);
        let payments = [];
        let diff = peoplePath[0].dept; // diff > 0 { payment from previous to next person }; diff < 0 { payment from next to previous person }
        for(let i = 1; i < peoplePath.length; i++) {
            let previousPerson = peoplePath[i-1];
            let thisPerson = peoplePath[i];

            if(diff > 0)  {
                payments.push({
                    from: previousPerson,
                    to: thisPerson,
                    amount: diff
                });
            } else {
                payments.push({
                    from: thisPerson,
                    to: previousPerson,
                    amount: -diff
                });
            }

            diff = thisPerson.dept + diff;
        }
        return payments;
    }
}

// returns the reverse of an array without changing the original
function reverseCopy(arr) {
    let len = arr.length;
    let out = new Array(len);
    for(let i in arr) {
        out[i] = arr[(len - 1) - i];
    }
    return out;
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