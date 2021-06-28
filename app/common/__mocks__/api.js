const api = jest.requireActual("../api");

const UnexpectedServerError = api.default.UnexpectedServerError;

const authenticate = jest.fn().mockResolvedValue({});
const getUserInfo = jest.fn().mockResolvedValue({});
const register = jest.fn().mockResolvedValue({});
const completeRegistration = jest.fn().mockResolvedValue({});
const getAllUserEvents = jest.fn().mockResolvedValue({});
const createEvent = jest.fn().mockResolvedValue({});
const getEvent = jest.fn().mockResolvedValue({});
const saveEvent = jest.fn().mockResolvedValue({});
const leaveEvent = jest.fn().mockResolvedValue({});
const inviteToEvent = jest.fn().mockResolvedValue({});
const bounceFromEvent = jest.fn().mockResolvedValue({});
const addExpenseSharingModule = jest.fn().mockResolvedValue({});
const addPaymentToExpenseSharing = jest.fn().mockResolvedValue({});
const deletePaymentFromExpenseSharing = jest.fn().mockResolvedValue({});

export {
    authenticate,
    getUserInfo,
    register,
    completeRegistration,
    getAllUserEvents,
    createEvent,
    getEvent,
    saveEvent,
    leaveEvent,
    inviteToEvent,
    bounceFromEvent,
    addExpenseSharingModule,
    addPaymentToExpenseSharing,
    deletePaymentFromExpenseSharing,
    UnexpectedServerError
};
