import { ViewState, EditingState, IntegratedEditing } from "@devexpress/dx-react-scheduler"
import { Scheduler, WeekView, Appointments, AppointmentForm } from "@devexpress/dx-react-scheduler-material-ui"
import { useEffect, useState } from 'react';
import axios from "axios";
import moment from "moment";
import { ListItemButton,  ListItemText, ListItem, List, Button, ListItemIcon } from '@mui/material';
import TextField from '@mui/material/TextField';
import Dialog from '@mui/material/Dialog';
import DialogActions from '@mui/material/DialogActions';
import DialogContent from '@mui/material/DialogContent';
import DialogContentText from '@mui/material/DialogContentText';
import DialogTitle from '@mui/material/DialogTitle';
import { DateTimePicker } from '@mui/x-date-pickers';
import { LocalizationProvider } from '@mui/x-date-pickers/LocalizationProvider';
import { AdapterDateFns } from '@mui/x-date-pickers/AdapterDateFns';
import { set } from "date-fns";
function isEmpty(obj) {
    return Object.keys(obj).length === 0;
  }
  

  function isIterable(obj) {
    // checks for null and undefined
    if (obj == null) {
      return false;
    }
    return typeof obj[Symbol.iterator] === 'function';
  }

  function isObject(obj)
{
    return obj != null && obj.constructor.name === "Object"
}
  


const Calendar = () => {


  const [schedule, setSchedule] = useState(false);
  const [isAdmin, setIsAdmin] = useState(false);
  const [showAdmin, setShowAdmin] = useState(false);
  const [open, setOpen] = useState(false);
  const [current, setCurrent] = useState(false);
  const [clearedDate, setClearedDate] = useState(null);
  const [clearedDate2, setClearedDate2] = useState(null);

  const isConnected = async () => {
    try {
     
      const { data } = await axios.get(API_BASE + `api/v1/availabilities`, {
        headers: {
            'Content-Type': 'application/json;charset=UTF-8',
        },
      })

      const dates = handleDBReponse(data);

      if (dates !== undefined ) {
        console.log("dates found");
        setSchedule(dates);
      } else {
        alert("No dates found");
      }

    } catch (error) {
      console.log(error);
    } 
  }

  const API_BASE = "http://localhost:8080/";

const schedulerData = [
    {startDate: '2022-07-24T11:00', endDate: '2022-07-24T12:00', title: "Test 1", id: "1"},
    {startDate: '2022-07-26T11:00', endDate: '2022-07-26T12:00', title: "Test 2", id:"2"},
];

const updateAppointment = (data) => {

    let id;

    if (data.hasOwnProperty('deleted')) {
        console.log("appointment deleted...")

        id = data['deleted'];
        console.log(`i will delete ${id}`)

        axios.delete(API_BASE + `api/v1/availabilities/${id}`,
        {
           headers: {
               'Content-Type': 'application/json;charset=UTF-8',
           },
         }
        )
       .then(function (response) {
           console.log(response);
         })
         .catch(function (error) {
           console.log(error);
         });
        

    }


    if (data.hasOwnProperty('changed')) {
        console.log("appointment upDATED")
        console.log(data);

        const appointment = data.changed;

        const payload = {};

            for (const [key, value] of Object.entries(appointment)) {
                console.log(`${key}: ${value}`);
    
                payload["id"] = key;
                id = key;
    
                for (const k in value) {
    
                    console.log(`${k}: ${value[k]}`);
    
                    if (k === "startDate") {
                        payload["startTime"] = moment.utc(value[k]).toISOString();
    
                    }
    
                    if (k === "endDate") {
                        payload["endTime"] = moment.utc(value[k]).toISOString();
                    }
                }
              }
    
              console.log(payload);

        

         axios.patch(API_BASE + `api/v1/availabilities/${id}`, payload,
         {
            headers: {
                'Content-Type': 'application/json;charset=UTF-8',
            },
          }
         )
        .then(function (response) {
            console.log(response);
            id = response.data.id;
            console.log(id);


          })
          .catch(function (error) {
            console.log(error);
          });
        
       
    }

    if (data.hasOwnProperty('added')) {
        console.log("appointment SAVEED");
        const appointment = data.added;

        const payload = {};


        if (isIterable(appointment)) {


            for (const [key, value] of Object.entries(appointment)) {
                console.log(`${key}: ${value}`);

                for (const k in value) {

                    console.log(`${k}: ${value[k]}`);

                    if (k === "startDate") {
                        payload["startTime"] = moment(value[k]).toISOString();

                    }

                    if (k === "endDate") {
                        payload["endTime"] = moment.utc(value[k]).toISOString();
                    }
                }
            }
            console.log(payload);

        } else {
            console.log("not array");
            payload["startTime"] = moment.utc(appointment.startDate).toISOString();
            payload["endTime"] = moment.utc(appointment.endDate).toISOString();

        }

       //const res = postData(payload);
       //console.log(res);
       axios.post(API_BASE + `api/v1/availabilities`, payload,
       {
          headers: {
              'Content-Type': 'application/json;charset=UTF-8',
          },
        })
       .then(function (response) {
        console.log(response);
        id = response.data.id;
        commitChanges(data, id);
        return;

      })
      .catch(function (error) {
        console.log(error);
      });

        
    }

    commitChanges(data, id);
    
}


const postData =  async (payload) => {

    

try {
    const { data:id } = await axios.post(API_BASE + `api/v1/availabilities`, payload,
    {
       headers: {
           'Content-Type': 'application/json;charset=UTF-8',
       },
     });

     return id;

    } catch (error) {
        console.log(error);
    } 

      
}


const commitChanges = ({ added, changed, deleted }, startingAddedId) => {

    let data = schedule;
    
      if (added) {
        if (isIterable(data)) {
            data = [...data, { id: startingAddedId, ...added }];
        } else {
            data = [{ id: startingAddedId, ...added }];
        }
       
    
      }
      if (changed) {
        data = data.map(appointment => (
          changed[appointment.id] ? { ...appointment, ...changed[appointment.id] } : appointment));
      }
      if (deleted !== undefined) {

        data = data.filter(appointment => appointment.id !== deleted);
      
      }

      setSchedule(data);
  }


const transformAppointmentData = (appointmentData) => {
  let data = [];

  appointmentData.forEach( appointment => {
    data.push({
      title: appointment.title,
      startDate:  moment(appointment.startTime).toDate(),
      endDate:  moment(appointment.endTime).toDate(),
      id:  appointment.id,
    })
  });

  return data;
}

 const handleDBReponse = (response) => {      

      const appointments = response.data;
      const startWeek = moment().startOf("week"); //start of today 12 am

      if (appointments.length  > 0) {
        return transformAppointmentData(appointments)
      }

      return undefined;

}

const handleListItemClick = (key) => 
{
    console.log("hello from list: "+ key);
    setOpen(true);

    setClearedDate(key.startDate)
    setClearedDate2(key.endDate)

    setCurrent(key);
}


useEffect( () => {
    isConnected();
   //alert("page has loaded");
  }, []);

  const handleClickOpen = () => {
        setOpen(true);

  };

  const handleClose = () => {

        setOpen(false);
       
  };

  const handleUpdateEnd = (end) => {

    setClearedDate2(end)
   
};


const handleUpdateStart = (start) => {

    setClearedDate(start)
   
};


  const handleSubmit = () => {
    const emailStr = document.getElementById("email-to-send").value;
    const title = document.getElementById("title-to-send").value;

    setOpen(false);
    console.log(emailStr);
    console.log(current);

    const payload = {};

    payload["startTime"] = moment(clearedDate).toISOString();
    payload["endTime"] = moment.utc(clearedDate2).toISOString();
    payload["availabilityId"] = current.id;
    payload["email"] = emailStr;
    payload["title"] = title;


    axios.post(API_BASE + `api/v1/reservations`, payload,
    {
       headers: {
           'Content-Type': 'application/json;charset=UTF-8',
       },
     })
    .then(function (response) {
     console.log(response);
     refreshData();

   })
   .catch(function (error) {
     console.log(error);
   });


  
  }


  const refreshData = async () => {
    try {
     
        const { data } = await axios.get(API_BASE + `api/v1/availabilities`, {
          headers: {
              'Content-Type': 'application/json;charset=UTF-8',
          },
        })
    
        const dates = handleDBReponse(data);
    
        if (dates !== undefined ) {
          console.log("dates found");
          setSchedule(dates);
        } 
    
      } catch (error) {
        console.log(error);
      } 
            
  }




  const Admin = () => {
    return <div>
        <List sx={{ width: '100%', maxWidth: 360 }}>
        {schedule && schedule.map(f => (
          <ListItemButton alignItems="flex-start" key={f.id} onClick={() => handleListItemClick(f)}>
            <ListItemText
              primary={moment(f.startDate).format('LLL')}
              secondary={moment(f.endDate).format('LLL')}
            />

          </ListItemButton>
        ))}


</List>

<Dialog open={open} onClose={() => handleClose()}>
        <DialogTitle>Reservation</DialogTitle>
        <DialogContent>
          <DialogContentText>
            Create a reservation, please enter your email address here, title and times that fit best for you.
            <br/>
            <br/>

          </DialogContentText>

          <LocalizationProvider dateAdapter={AdapterDateFns}>
<DateTimePicker
          value={clearedDate}
          label="Start time"
          minTime={current.startDate}
          maxTime={current.endDate}
          onChange={(newValue) => handleUpdateStart(newValue)}
          renderInput={(params) => (
            <TextField {...params} helperText="Clear Initial State" />
          )}
        />

<DateTimePicker
          value={clearedDate2}
          label="End time"
          minTime={current.startDate}
          maxTime={current.endDate}
          onChange={(newValue) => handleUpdateEnd(newValue)}
          renderInput={(params) => (
            <TextField {...params} helperText="Clear Initial State" />
          )}
        />
            </LocalizationProvider>
          <TextField
            autoFocus
            margin="dense"
            id="email-to-send"
            label="Email Address"
            type="email"
            fullWidth
            variant="standard"
          />

<TextField
            margin="dense"
            id="title-to-send"
            label="title"
            type="text"
            fullWidth
            variant="standard"
          />
        </DialogContent>
        <DialogActions>
          <Button onClick={() => handleClose()}>Cancel</Button>
          <Button onClick={() => handleSubmit()}>Choose</Button>
        </DialogActions>
      </Dialog>
    </div>
  }

  if (!schedule) {
    return <div>
    {<Admin/>}
    return <div id="calendar">
    <Scheduler>
        <ViewState/>
        <EditingState onCommitChanges={updateAppointment}/>
        <IntegratedEditing/>
        <WeekView startDayHour={9} endDayHour={19}/>
        <Appointments/>
        <AppointmentForm/>
    </Scheduler>
</div>
</div>;
  } else {
    return <div>
    {<Admin/>}
    <div id="calendar">
        <Scheduler data={schedule}>
            <ViewState/>
            <EditingState onCommitChanges={updateAppointment}/>
            <IntegratedEditing/>
            <WeekView startDayHour={9} endDayHour={19}/>
            <Appointments/>
            <AppointmentForm/>
        </Scheduler>
    </div>
    </div>;
  }

    
}

export default Calendar;