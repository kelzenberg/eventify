import React from 'react';

export default class ErrorBoundary extends React.Component {
    constructor(props) {
        super(props);
        this.state = { hasError: false };
    }
  
    static getDerivedStateFromError(error) {
        return { hasError: true };
    }
  
    componentDidCatch(error, errorInfo) {
        console.warn("Error Boundary catched:", errorInfo);
        console.warn(error);
    }
  
    render() {
        if (this.state.hasError) {
            return this.props.errorComponent;
        }
    
        return this.props.children; 
    }
}