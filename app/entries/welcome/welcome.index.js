import React from 'react';
import ReactDOM from 'react-dom';
import { BrowserRouter, Route, Switch } from 'react-router-dom';
import LoadingPage from '../../pages/Loading';
import "../../common.scss";

const LandingPage = React.lazy(() => import('../../pages/Landing/Landing'));

ReactDOM.render(<WelcomePage/>, document.getElementById('root'));

function WelcomePage() {
    return <BrowserRouter>
        <React.Suspense fallback={<LoadingPage/>}>
            <Switch>
                <Route path="/">
                    <LandingPage/>
                </Route>
            </Switch>
        </React.Suspense>
    </BrowserRouter>
}