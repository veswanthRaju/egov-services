import React, { Component } from 'react';
import {connect} from 'react-redux';
// import PropTypes from 'prop-types';

import Dialog from 'material-ui/Dialog';
import FlatButton from 'material-ui/FlatButton';
// import {
//   Redirect,
//   withRouter
// } from 'react-router-dom';
import { withRouter } from 'react-router'

import Header from './common/Header';
import Footer from './common/Footer';
// import PropertyTaxSearch from "./contents/PropertyTaxSearch";
import Snackbar from 'material-ui/Snackbar';
import LoadingIndicator from './common/LoadingIndicator';
import router from "../router";
//api import
// import api from "../api/commonAPIS"


window.urlCheck = false;
class App extends Component {

  // constructor(props) {
  //     super(props);
  //     // this.getOtp = this.getOtp.bind(this);
  //     // this.validateOtp = this.validateOtp.bind(this);
  //     // this.callLogin = this.callLogin.bind(this);
  // }

  // componentDidUpdate(prevProps) {
  //   // const { location} = this.props;
  //   // console.log(location);
  //
  //   // if (window.localStorage.getItem("token")) {
  //   //     this.props.history.replace(location.location);
  //   // }
  //   // else {
  //   //     this.props.history.replace("/");
  //   // }
  //
  //   // console.log(this.props);
  //   // console.log(match);
  //   // const isLoggingOut = prevProps.isLoggedIn && !this.props.isLoggedIn
  //   // const isLoggingIn = !prevProps.isLoggedIn && this.props.isLoggedIn
  //   //
  //   // if (isLoggingIn) {
  //   //   dispatch(navigateTo(redirectUrl))
  //   // } else if (isLoggingOut) {
  //   //
  //   //   // do any kind of cleanup or post-logout redirection here
  //   // }
  // }

  // componentWillUpdate()
  // {
  //   // console.log("hit");
  //     const { location} = this.props;
  //     console.log(location);
  //
  //     if (window.localStorage.getItem("token")) {
  //         this.props.history.replace(location.location);
  //     }
  //     else {
  //         this.props.history.replace("/");
  //     }
  // }

  componentWillReceiveProps(nextProps) {
      if (nextProps.redirectTo) {
            // console.log("Login");
            this.props.history.replace(nextProps.redirectTo);
            this.props.onRedirect();
      }



      // console.log(this.props.location);
      // if (!window.localStorage.getItem("token") && this.props.location.pathname!="/") {
      //     // console.log("Login");
      //   // if (this.props.location.pathname=="/pgr/createGrievance") {
      //   //     this.props.history.replace("/pgr/createGrievance");
      //   // } else {
      //     this.props.history.replace("/");
      //
      //   // }
      //   // this.props.onRedirect();
      // }

      // if (this.props.location.pathname=="/pgr/createGrievance") {
      //     this.props.history.replace("/pgr/createGrievance");
      // }
  }

  componentWillMount() {
    let {history}=this.props;
    // let commonState=JSON.parse(window.localStorage.getItem("reduxPersist:common"));
    // console.log(commonState);
    //if (!window.localStorage.getItem("token")) {
      //window.location.href = "/";
    //}

      // this.props.setLabels(agent.labels.getLabels());
      // const token = window.localStorage.getItem('jwt');
      // const userId = window.localStorage.getItem('userId');
      // const type = window.localStorage.getItem('type');
      //
      // let currentUser = window.localStorage.getItem('currentUser');
      //
      // if(currentUser) {
      //     currentUser = JSON.parse(currentUser);
      // }
      //
      // if (token) {
      //     // agent.setToken(token);
      //     // agent.setUserId(userId);
      //     // agent.setType(type);
      // }

      // api.commonApiPost("user/oauth/token",{tenantId:"default",
      //     username:"narasappa",
      //     password:"demo",
      //     grant_type:"password",
      //     scope:"read"}).then((response)=>{
      //       console.log(response);
      //     },(err)=> {
      //     console.log(err);
      //   });



      // this.props.onLoad(!currentUser
      //     ? agent.Auth.login((this.props.auth.userName || "9999999999"), (this.props.auth.password || "demo"))
      //     : {UserRequest: currentUser}, token);

      // console.log("hit");

      if(localStorage.getItem("token") && localStorage.getItem("userRequest")) {
        this.props.onLoad({UserRequest: JSON.parse(localStorage.getItem("userRequest"))}, localStorage.getItem("token"));
      }
  }


  handleClose = () => {
    this.props.toggleDailogAndSetText(false, '');
  }

  render() {
    var {toggleDailogAndSetText,toggleSnackbarAndSetText, isDialogOpen, msg, token, history, isSnackBarOpen, toastMsg, loadingStatus} = this.props;

    const actions = [
      <FlatButton
        label="Ok"
        primary={true}
        onTouchTap={this.handleClose}
      />,
    ];
    return (
      <div className="App">
          <Header/>

              {router}
          <Footer/>

          <Dialog
            actions={actions}
            modal={true}
            open={isDialogOpen}
            onRequestClose={()=>toggleDailogAndSetText(false,"")}
            >
            {msg}
          </Dialog>
          <Snackbar
              open={isSnackBarOpen}
              message={toastMsg}
              autoHideDuration={6000}
              onRequestClose={()=>toggleSnackbarAndSetText(false,"")}
            />
          <LoadingIndicator status={loadingStatus || "hide"}/>
      </div>
    );
  }
}

const mapStateToProps = state => ({
    // labels: state.labels,
    // appLoaded: state.common.appLoaded,
    // appName: state.common.appName,
    // currentUser: state.common.currentUser,
    // route:state.common.route,
    redirectTo: state.common.redirectTo,
    token:state.common.token,
    // pleaseWait: state.common.pleaseWait,
    // token:state.common.token,
    isDialogOpen: state.form.dialogOpen,
    msg: state.form.msg,
    isSnackBarOpen : state.form.snackbarOpen,
    toastMsg: state.form.toastMsg,
    loadingStatus: state.form.loadingStatus
});

// this.props.appLoaded

const mapDispatchToProps = dispatch => ({
    onLoad: (payload, token) => dispatch({type: 'APP_LOAD', payload, token, skipTracking: true}),
    onRedirect: () => dispatch({type: 'REDIRECT'}),
    setRoute:(route)=>dispatch({type:'SET_ROUTE',route}),
    // setLabels: payload => dispatch({type: 'LABELS', payload}),
    // onUpdateAuth: (value, key) => dispatch({type: 'UPDATE_FIELD_AUTH', key, value}),
    // onLogin: (username, password) => {
    //     dispatch({
    //         type: 'LOGIN',
    //         payload: []//agent.Auth.login(username, password)
    //     })
    // },
    // updateError: (error) =>
    //     dispatch({
    //         type: 'UPDATE_ERROR',
    //         error
    //     }),
    // setPleaseWait: (pleaseWait) =>
    //     dispatch({
    //         type: 'PLEASE_WAIT',
    //         pleaseWait
    //     }),
    toggleDailogAndSetText: (dailogState,msg) => {
      dispatch({type: "TOGGLE_DAILOG_AND_SET_TEXT", dailogState, msg});
    },
    toggleSnackbarAndSetText: (snackbarState, toastMsg) => {
      dispatch({type: "TOGGLE_SNACKBAR_AND_SET_TEXT", snackbarState, toastMsg});
    },
    setLoadingStatus: (loadingStatus) => {
      dispatch({type: "SET_LOADING_STATUS", loadingStatus});
    }
});


// App.contextTypes = {
//     router: React.PropTypes.object.isRequired
// };




export default withRouter(connect(mapStateToProps, mapDispatchToProps)(App));
