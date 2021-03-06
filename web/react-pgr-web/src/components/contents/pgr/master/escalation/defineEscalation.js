import React, {Component} from 'react';
import {connect} from 'react-redux';
import {Grid, Row, Col, DropdownButton, Table, ListGroupItem} from 'react-bootstrap';
import {Card, CardHeader, CardText} from 'material-ui/Card';
import TextField from 'material-ui/TextField';
import {brown500, red500,white,orange800} from 'material-ui/styles/colors';
import Checkbox from 'material-ui/Checkbox';
import DatePicker from 'material-ui/DatePicker';
import SelectField from 'material-ui/SelectField';
import MenuItem from 'material-ui/MenuItem';
import AutoComplete from 'material-ui/AutoComplete';
import Dialog from 'material-ui/Dialog';
import RaisedButton from 'material-ui/RaisedButton';
import FlatButton from 'material-ui/FlatButton';
import DataTable from '../../../../common/Table';
import Api from '../../../../../api/api';
import {translate} from '../../../../common/common';


const $ = require('jquery');
$.DataTable = require('datatables.net');
const dt = require('datatables.net-bs');

const buttons = require('datatables.net-buttons-bs');

require('datatables.net-buttons/js/buttons.colVis.js'); // Column visibility
require('datatables.net-buttons/js/buttons.html5.js'); // HTML 5 file export
require('datatables.net-buttons/js/buttons.flash.js'); // Flash file export
require('datatables.net-buttons/js/buttons.print.js'); // Print view button

var flag = 0;
const styles = {
  headerStyle : {
    color: 'rgb(90, 62, 27)',
    fontSize : 19
  },
  marginStyle:{
    margin: '15px'
  },
  paddingStyle:{
    padding: '15px'
  },
  errorStyle: {
    color: red500
  },
  underlineStyle: {
    borderColor: brown500
  },
  underlineFocusStyle: {
    borderColor: brown500
  },
  floatingLabelStyle: {
    color: brown500
  },
  floatingLabelFocusStyle: {
    color: brown500
  },
  customWidth: {
    width:100
  },
  checkbox: {
    marginTop: 37
  }
};

const getNameById = function(object, id, property = "") {


  if (id == "" || id == null) {
        return "";
    }
    for (var i = 0; i < object.length; i++) {
        if (property == "") {
            if (object[i].id == id) {
                return object[i].name;
            }
        } else {
            if (object[i].hasOwnProperty(property)) {
                if (object[i].id == id) {
                    return object[i][property];
                }
            } else {
                return "";
            }
        }
    }
    return "";
}

const getNameByServiceCode = function(object, serviceCode, property = "") { 

  if (serviceCode == "" || serviceCode == null) {
        return "";
    }
    for (var i = 0; i < object.length; i++) {
        if (property == "") {
            if (object[i].serviceCode == serviceCode) {
                return object[i].serviceName;
            }
        } else {
            if (object[i].hasOwnProperty(property)) {
                if (object[i].serviceCode == serviceCode) {
                    return object[i][property];
                }
            } else {
                return "";
            }
        }
    }
    return "";
}

class DefineEscalation extends Component {
    constructor(props) {
      super(props)
      this.state = {
              positionSource:[],
              dataSourceConfig : {
                text: 'name',
                value: 'id',
              },
              isSearchClicked: false,
              resultList: [],
              noData:false,
              escalationForm:{
              },
			  editIndex: -1,
			  grievanceType:[],
			  designations:[],
			  departments:[],
			  toPosition: [],
        localDesignation:[]
            };
    }

    componentWillMount() {

      let{initForm} = this.props;
      initForm();

      $('#searchTable').DataTable({
           dom: 'lBfrtip',
           buttons: [
                     'excel', 'pdf', 'print'
            ],
            ordering: false,
            bDestroy: true,
      });
    }

    componentDidMount() {
      let self = this;
      Api.commonApiPost("/hr-masters/positions/_search").then(function(response) {
          self.setState({
            positionSource: response.Position
          })
      }, function(err) {
        self.setState({
            positionSource: []
          })
      });
	  
	  
		Api.commonApiPost("/pgr/services/v1/_search", {type: "all"}).then(function(response) {
			self.setState({
			  grievanceType: response.complaintTypes
			})
		}, function(err) {
		  self.setState({
			  grievanceType: []
			})
		});
		
		
		Api.commonApiPost("/egov-common-masters/departments/_search", {}).then(function(response) {
			self.setState({
			  departments: response.Department
			})
        }, function(err) {
        self.setState({
            departments: []
          })
      });

    Api.commonApiPost("/hr-masters/designations/_search",{}).then(function(response) {
        self.setState({localDesignation : response.Designation});
        },function(err) {
      
    }); 
}

componentWillUpdate() {
	  $('#searchTable').dataTable().fnDestroy();
 }


    componentDidUpdate() {
 
         $('#searchTable').DataTable({
         dom: 'lBfrtip',
         buttons: [],
          bDestroy: true,
          language: {
             "emptyTable": "No Records"
          }
    });
    
    }


    componentWillUnmount(){
       $('#searchTable')
       .DataTable()
       .destroy(true);
    }

  submitForm = (e) => {

     let {setLoadingStatus, toggleSnackbarAndSetText} = this.props;
    setLoadingStatus('loading');

      e.preventDefault();
      let current = this;

      let query = {
        fromPosition:this.props.defineEscalation.fromPosition
      }
	  
      Api.commonApiPost("/pgr-master/escalation-hierarchy/v1/_search",query,{}).then(function(response){
           setLoadingStatus('hide');

          if (response.escalationHierarchies[0] != null) {
              flag = 1;
              current.setState({
                resultList: response.escalationHierarchies,
                isSearchClicked: true
              })

          } else {
            current.setState({
              noData: true,
            })
          }
      }).catch((error)=>{
         toggleSnackbarAndSetText(true, error);
      })
  }

 
	handleDepartment = (e) => {
		
		 let {toggleSnackbarAndSetText, emptyProperty} = this.props;
		
		 var currentThis = this;
	    currentThis.setState({designations : []});
		  currentThis.setState({toPosition : []});
		
		let query = {
			id:e.target.value
		}
	
		  Api.commonApiPost("/hr-masters/designations/_search",query).then(function(response)
		  {
			currentThis.setState({designations : response.Designation});
		  },function(err) {
			toggleSnackbarAndSetText(true, err);
		  });	
  }
  
  handleDesignation = (e) => { 

  let {setLoadingStatus} = this.props;
		
		var current = this;
		this.setState({toPosition : []});
		
		let query = {
			departmentId:this.props.defineEscalation.department, 
			designationId:e.target.value
		}
	
		  Api.commonApiPost("/hr-masters/positions/_search",query).then(function(response)
		  {console.log(response);
        setLoadingStatus('hide');
			current.setState({toPosition : response.Position});
		  },function(err) {
			console.log(err);
		  });	
  }
  
  
  updateEscalation = () => {

    let {setLoadingStatus, toggleSnackbarAndSetText, toggleDailogAndSetText, emptyProperty} = this.props;
    setLoadingStatus('loading');

		 var current = this
    var body = {
      escalationHierarchy: [ {
				serviceCode : this.props.defineEscalation.serviceCode,
				tenantId : "default",
				fromPosition : this.props.defineEscalation.fromPosition,
				toPosition : this.props.defineEscalation.toPosition,
        department :this.props.defineEscalation.department,
        designation :this.props.defineEscalation.designation
			  }]
    }
	
	//var idd = this.props.defineEscalation.fromPosition;

    Api.commonApiPost("/pgr-master/escalation-hierarchy/v1/_update/",{},body).then(function(response){
		toggleDailogAndSetText(true, "Escalation Updated Successfully");
          let query = {
        fromPosition:current.props.defineEscalation.fromPosition
      }
		emptyProperty("serviceCode");
		emptyProperty("department");
		emptyProperty("designation");
		emptyProperty("toPosition");
                Api.commonApiPost("/pgr-master/escalation-hierarchy/v1/_search",query,{}).then(function(response){
                    setLoadingStatus('hide');
                    if (response.escalationHierarchies[0] != null) {
                        flag = 1;
                        current.setState({
                          resultList: response.escalationHierarchies,
                          isSearchClicked: true
                        })

                    } else {
                      current.setState({
                        noData: true,
                      })
                    }
                }).catch((error)=>{
					 toggleSnackbarAndSetText(true, error);
                })

              current.setState((prevState)=>{
                  prevState.editIndex=-1
                })
    }).catch((error)=>{
		toggleSnackbarAndSetText(true, error);
    })
  }

  addEscalation = () => {

    let {setLoadingStatus, toggleDailogAndSetText, toggleSnackbarAndSetText, emptyProperty} = this.props;
    setLoadingStatus('loading');

    var current = this
    var body = {
      escalationHierarchy: [ {
				serviceCode : this.props.defineEscalation.serviceCode,
				tenantId : "default",
				fromPosition : this.props.defineEscalation.fromPosition,
				toPosition : this.props.defineEscalation.toPosition,
        department :this.props.defineEscalation.department,
        designation :this.props.defineEscalation.designation
			  }]
    }

    Api.commonApiPost("/pgr-master/escalation-hierarchy/v1/_create",{},body).then(function(response){
		toggleDailogAndSetText(true, "Escalation Created Successfully");
        let query = {
        fromPosition:current.props.defineEscalation.fromPosition
		}
		
		emptyProperty("serviceCode");
		emptyProperty("department");
		emptyProperty("designation");
		emptyProperty("toPosition");
    
                Api.commonApiPost("/pgr-master/escalation-hierarchy/v1/_search",query,{}).then(function(response){
                    setLoadingStatus('hide');
                    if (response.escalationHierarchies[0] != null) {
                        flag = 1;
                        current.setState({
                          resultList: response.escalationHierarchies,
                          isSearchClicked: true
                        })

                    } else {
                      current.setState({
                        noData: true,
                      })
                    }
                }).catch((error)=>{
                    toggleSnackbarAndSetText(true, error);
                })
       
    }).catch((error)=>{
		toggleSnackbarAndSetText(true, error);
    })
  }
  
  editObject = (index) => {
      let {setLoadingStatus} = this.props;
      setLoadingStatus('loading');
      this.props.setForm(this.state.resultList[index])

      var d = {
        target: {
          value: this.state.resultList[index].department
        }
      }

       var e = {
        target: {
          value: this.state.resultList[index].designation
        }
      }

      this.handleDepartment(d);

      this.handleDesignation(e);
  }

  
  localHandleChange = (e, property, isRequired, pattern) => {
    if(this.state.escalationForm.hasOwnProperty('fromPosition')){
      this.setState({
          escalationForm: {
            ...this.state.escalationForm,
              [property]: e.target.value
          }
      })
    } else {
      this.setState({
          escalationForm: {
            ...this.state.escalationForm,
            fromPosition: this.props.defineEscalation.position.id,
              [property]: e.target.value
          }
      })
    }
  }

    render() {

      var current = this;

      let {
        isFormValid,
        defineEscalation,
        fieldErrors,
        handleChange,
        handleAutoCompleteKeyUp
      } = this.props;

      let {submitForm, localHandleChange, addEscalation, updateEscalation, editObject} = this;


      let {isSearchClicked, resultList, escalationForm,  editIndex} = this.state;

      const renderBody = function() {
      	  if(resultList && resultList.length)
      		return resultList.map(function(val, i) {
      			return (
      				<tr key={i}>
                <td>{getNameById(current.state.positionSource,val.fromPosition)}</td>
      					<td>{val.serviceCode ? getNameByServiceCode(current.state.grievanceType ,val.serviceCode) : ""}</td>
      					<td>{getNameById(current.state.departments,val.department)}</td>
      					<td>{getNameById(current.state.localDesignation,val.designation)}</td>
                <td>{getNameById(current.state.positionSource,val.toPosition)}</td>
                <td><RaisedButton primary={true} style={{margin:'15px 5px'}} label={translate("pgr.lbl.edit")} onClick={() => {
					           editObject(i);
                    current.setState({editIndex:i})
                    }}/></td>
      				</tr>
      			)
      		})
      }

      const viewTable = function() {
      	  if(isSearchClicked)
      		return (
   	        <Card>
              <CardText>
                  <Row>
                    <Col xs={12} md={3} sm={6}>
                        <TextField
                            fullWidth={true}
                            floatingLabelText={translate('pgr.lbl.fromposition')+" *"}
                            value={defineEscalation.fromPosition ? getNameById(current.state.positionSource, defineEscalation.fromPosition) : ""}
                            id="name"
                            disabled={true}
                        />
                    </Col>
                    <Col xs={12} md={3} sm={6}>
                        <SelectField
                           floatingLabelText={translate('pgr.lbl.grievance.type')+ " *"}
                           fullWidth={true}
                           value={defineEscalation.serviceCode ? defineEscalation.serviceCode : ""}
						   disabled={editIndex<0 ? false : true}
                           onChange= {(e, index ,value) => {
                             var e = {
                               target: {
                                 value: value
                               }
                             };
                             handleChange(e, "serviceCode", true, "");
                            }}
                          >
						  {current.state.grievanceType && current.state.grievanceType.map((item, index)=>{
							  return(
								<MenuItem value={item.serviceCode} key={index} primaryText={item.serviceName} />
							  )
						  })}
                        </SelectField>
                    </Col>
                    <Col xs={12} md={3} sm={6}>
                        <SelectField
                           floatingLabelText={translate('core.lbl.department') + ' *'}
                           fullWidth={true}
                           value={defineEscalation.department ? defineEscalation.department : ""}
                           onChange= {(e, index ,value) => {
                             var e = {
                               target: {
                                 value: value
                               }
                             };
							 current.handleDepartment(e);
                             handleChange(e, "department", true, "");
                            }}
                         >
							 {current.state.departments && current.state.departments.map((item, index)=>{
								 return(
									<MenuItem value={item.id} key={index} primaryText={item.name} />
								 )
							})}
                           
                        </SelectField>
                    </Col>
                    <Col xs={12} md={3} sm={6}>
                        <SelectField
                           floatingLabelText={translate('pgr.lbl.designation')+" *"}
                           fullWidth={true}
                           value={defineEscalation.designation ? defineEscalation.designation : ""}
                           onChange= {(e, index ,value) => {
                             var e = {
                               target: {
                                 value: value
                               }
                             };
							 current.handleDesignation(e);
                             handleChange(e, "designation", true, "");
                            }}
                         >
                           {current.state.designations && current.state.designations.map((item, index)=>{
								 return(
									<MenuItem value={item.id} key={index} primaryText={item.name} />
								 )
							})}
                        </SelectField>
                    </Col>
                    <Col xs={12} md={3} sm={6}>
                        <SelectField
                           floatingLabelText={translate('pgr.lbl.toposition')+" *"}
                           fullWidth={true}
                           value={defineEscalation.toPosition ?  defineEscalation.toPosition  : ""}
                           onChange= {(e, index ,value) => {
                             var e = {
                               target: {
                                 value: value
                               }
                             };
                             handleChange(e, "toPosition", true, "");
                            }}
                         >
                            {current.state.toPosition && current.state.toPosition.map((item, index)=>{
								 return(
									<MenuItem value={item.id} key={index} primaryText={item.name ?  item.name: getNameById(current.state.toPosition, defineEscalation.toPosition)} />
								 )
							})}
                        </SelectField>
                    </Col>
                    <div className="clearfix"></div>
					<Col xs={12} md={12} style={{textAlign:"center"}}>
                        {editIndex<0 && <RaisedButton primary={true} style={{margin:'15px 5px'}} disabled={!isFormValid} label={translate("pgr.lbl.add")} primary={true} onClick={() => {
                          addEscalation();
                        }}/>}
                        {editIndex>=0 && <RaisedButton primary={true} style={{margin:'15px 5px'}} disabled={!isFormValid} label={translate("pgr.lbl.update")} primary={true} onClick={() => {

                          updateEscalation();
                        }}/>}
                    </Col>
                  </Row>
              </CardText>
   	          <CardText>
   		        <Table id="searchTable" style={{color:"black",fontWeight: "normal"}} bordered responsive>
   		          <thead >
   		            <tr>
						<th>{translate('pgr.lbl.fromposition')}</th>
						<th>{translate('pgr.lbl.grievance.type')}</th>
						<th>{translate('core.lbl.department')}</th>
						<th>{translate('pgr.lbl.designation')}</th>
						<th>{translate('pgr.lbl.toposition')}</th>
						<th></th>
   		            </tr>
   		          </thead>
   		          <tbody>
   		          	{renderBody()}
   		          </tbody>
   		        </Table>
   		       </CardText>
   		    </Card>
   		)
      }

      return(<div className="defineEscalation">
      <form autoComplete="off" onSubmit={(e) => {submitForm(e)}}>
          <Card  style={styles.marginStyle}>
              <CardHeader style={{paddingBottom:0}} title={< div style = {styles.headerStyle} > {translate('pgr.lbl.escalation')} < /div>} />
              <CardText>
                  <Card>
                      <CardText>
                          <Grid>
                              <Row>
                                  <Col xs={12} md={4} mdPush={4}>
                                        <AutoComplete
                                          floatingLabelText={translate('pgr.lbl.position')+" *"}
                                          fullWidth={true}
                                          filter={function filter(searchText, key) {
                                                    return key.toLowerCase().includes(searchText.toLowerCase());
                                                 }}
                                          dataSource={this.state.positionSource}
                                          dataSourceConfig={this.state.dataSourceConfig}
                                          value={defineEscalation.fromPosition ? defineEscalation.fromPosition : ""}
                                          onNewRequest={(chosenRequest, index) => {
                  	                        var e = {
                  	                          target: {
                  	                            value: chosenRequest.id
                  	                          }
                  	                        };
                  	                        handleChange(e, "fromPosition", true, "");
                  	                       }}
                                        />
                                  </Col>
                              </Row>
                          </Grid>
                      </CardText>
                  </Card>
                  <div style={{textAlign:'center'}}>

                      <RaisedButton primary={true} style={{margin:'15px 5px'}} type="submit" disabled={defineEscalation.fromPosition ? false: true} label={translate('core.lbl.search')} />

                  </div>
                  {this.state.noData &&
                    <Card style = {{textAlign:"center"}}>
                      <CardHeader title={<strong style = {{color:"#5a3e1b", paddingLeft:90}} > There is no escalation details available for the selected position. </strong>}/>
                      <CardText>
                          <RaisedButton primary={true} style={{margin:'10px 0'}} label={translate('pgr.lbl.addesc')} labelColor={white} onClick={() => {
                            this.setState({
                              isSearchClicked: true,
                              noData:false
                            })
                          }}/>
                     </CardText>
                  </Card>
                  }
                  {viewTable()}
              </CardText>
          </Card>
          </form>
      </div>)
    }
}


const mapStateToProps = state => {
  return ({defineEscalation : state.form.form, fieldErrors: state.form.fieldErrors, isFormValid: state.form.isFormValid});
}

const mapDispatchToProps = dispatch => ({
  initForm: () => {
    dispatch({
      type: "RESET_STATE",
      validationData: {
        required: {
          current: [],
          required: ["fromPosition", "serviceCode", "department", "designation", "toPosition"]
        },
        pattern: {
          current: [],
          required: []
        }
      }
    });
  },

  setForm: (data) => {
    dispatch({
      type: "SET_FORM",
      data,
      isFormValid:true,
      fieldErrors: {},
      validationData: {
        required: {
          current: ["fromPosition", "serviceCode", "department", "designation", "toPosition"] ,
          required:["fromPosition", "serviceCode", "department", "designation", "toPosition"]
        },
        pattern: {
          current: [],
          required: []
        }
      }
    });
  },
  emptyProperty: (property) => {
	  dispatch({
		  type: "EMPTY_PROPERTY",
		  property,
		  isFormValid:false,
		  validationData: {
        required: {
          current: ["fromPosition", ],
          required: ["fromPosition", "serviceCode", "department", "designation", "toPosition"]
        },
        pattern: {
          current: [],
          required: []
        }
		  }
		});
  },
  
  handleChange: (e, property, isRequired, pattern) => {
    dispatch({
      type: "HANDLE_CHANGE",
      property,
      value: e.target.value,
      isRequired,
      pattern
    });
  },

  handleAutoCompleteKeyUp : (e, type) => {
    dispatch({
      type: "HANDLE_CHANGE",
      property: type,
      value: e.target.value,
      isRequired : true,
      pattern: ''
    });
  },
     setLoadingStatus: (loadingStatus) => {
      dispatch({type: "SET_LOADING_STATUS", loadingStatus});
    },
	
	 toggleDailogAndSetText: (dailogState,msg) => {
      dispatch({type: "TOGGLE_DAILOG_AND_SET_TEXT", dailogState, msg});
    },

    toggleSnackbarAndSetText: (snackbarState, toastMsg) => {
      dispatch({type: "TOGGLE_SNACKBAR_AND_SET_TEXT", snackbarState, toastMsg});
    },
})

export default connect(mapStateToProps, mapDispatchToProps)(DefineEscalation);
