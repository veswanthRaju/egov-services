import React, {Component} from 'react';
import {connect} from 'react-redux';

import MenuItem from 'material-ui/MenuItem';
import {Grid, Row, Col, Table, DropdownButton} from 'react-bootstrap';
import {Card, CardHeader, CardText} from 'material-ui/Card';

import Api from '../../../api/api';
import {translate} from '../../common/common';


const $ = require('jquery');
$.DataTable = require('datatables.net');
const dt = require('datatables.net-bs');


const buttons = require('datatables.net-buttons-bs');

require('datatables.net-buttons/js/buttons.colVis.js'); // Column visibility
require('datatables.net-buttons/js/buttons.html5.js'); // HTML 5 file export
require('datatables.net-buttons/js/buttons.flash.js'); // Flash file export
require('datatables.net-buttons/js/buttons.print.js'); // Print view button




class ShowField extends Component {
  constructor(props) {
       super(props);

   }



   componentWillMount()
   {
     $('#searchTable').DataTable({
       dom: 'lBfrtip',
       buttons: [],
        bDestroy: true,
        language: {
           "emptyTable": "No Records"
        }
      });
   }

  componentWillUnmount()
  {
     $('#reportTable')
     .DataTable()
     .destroy(true);
  }




  componentWillUpdate() {
    let {flag}=this.props;
    if(flag == 1) {
      flag = 0;
      $('#reportTable').dataTable().fnDestroy();
    }
  }

  componentDidUpdate() {
          $('#reportTable').DataTable({
            dom: 'lBfrtip',
            buttons: [
                     'copy', 'csv', 'excel', 'pdf', 'print'
             ],
             ordering: false,
             bDestroy: true,

          });
  }

  render() {
    let {
      isTableShow,
      metaData,
      reportResult
    } = this.props;
    // console.log(metaData);

    const showRow=()=>{
      return
    }

    const viewTabel=()=>
    {
      return (
        <Card>
          <CardHeader title={< strong > Result < /strong>}/>
          <CardText>
          <Table id="reportTable" style={{color:"black",fontWeight: "normal"}} bordered responsive>
          <thead style={{backgroundColor:"#f2851f",color:"white"}}>
            <tr>
              {reportResult.hasOwnProperty("reportHeader") && reportResult.reportHeader.map((item,i)=>
              {
                return (
                  <th key={i}>{translate(item.label)}</th>
                )
              })}
            {/*
              <th>#</th>
                <th>Assessment Number</th>
                <th>Owner Name</th>
                <th>Address</th>
                <th>Current Demand</th>
                <th>Arrears Demand</th>
                <th>Property usage</th>*/}

            </tr>
          </thead>
          <tbody>

                {reportResult.hasOwnProperty("reportData") && reportResult.reportData.map((item,i)=>
                {
                  return (
                    <tr key={i}>
                      {item.map((item1,i2)=>{
                        return (
                          <td key={i2}>{item1}</td>
                        )
                      })}
                    </tr>
                    )

                })}


          </tbody>
        </Table>
      </CardText>
      </Card>
      )
    }
    return (
      <div className="PropertyTaxSearch">
        {isTableShow && reportResult.hasOwnProperty("reportHeader") && reportResult.hasOwnProperty("reportData") && viewTabel()}
      </div>
    );
  }
}

const mapStateToProps = state => ({isTableShow:state.form.showTable,metaData:state.report.metaData,reportResult:state.report.reportResult,flag:state.report.flag});

const mapDispatchToProps = dispatch => ({

});

export default connect(mapStateToProps, mapDispatchToProps)(ShowField);
