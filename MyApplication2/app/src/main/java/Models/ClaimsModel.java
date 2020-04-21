package Models;



public class ClaimsModel {
    String overView;
    String imageurl;
    String status;
    String claimid;


    String incident_date;

    public ClaimsModel(){

    }
    public ClaimsModel(String overView, String imageurl, String status) {
        this.overView = overView;
        this.imageurl = imageurl;
        this.status = status;
    }

    public String getOverView() {
        return overView;
    }

    public void setOverView(String overView) {
        this.overView = overView;
    }

    public String getImageurl() {
        return imageurl;
    }

    public void setImageurl(String imageurl) {
        this.imageurl = imageurl;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getClaimid() {
        return claimid;
    }

    public void setClaimid(String claimid) {
        this.claimid = claimid;
    }
    public String getIncident_date() {
        return incident_date;
    }

    public void setIncident_date(String incident_date) {
        this.incident_date = incident_date;
    }

}
