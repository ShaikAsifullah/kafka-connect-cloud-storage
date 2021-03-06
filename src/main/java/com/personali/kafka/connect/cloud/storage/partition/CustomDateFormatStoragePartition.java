package com.personali.kafka.connect.cloud.storage.partition;

import com.personali.kafka.connect.cloud.storage.parser.JsonRecordParser;
import org.apache.kafka.connect.sink.SinkRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

/**
 * Created by or on 27/12/16.
 */
public class CustomDateFormatStoragePartition extends StoragePartition {
    private static final Logger log = LoggerFactory.getLogger(TopicPartitionFiles.class);
    private String dataDirectory;
    private String indexDirectory;

    

    public CustomDateFormatStoragePartition(SinkRecord record, Map<String,String> props){
        super(record,props);
        String dateFormat = props.get("custom.date.partition.format");
        String dateField = props.get("custom.date.field");
        String dateFieldFormat = props.get("custom.date.field.format");
        String delimiter = props.getOrDefault("custom.date.field.delimiter",".");
        SimpleDateFormat df = new SimpleDateFormat(dateFormat);
        try {
            Date partitionDate = new JsonRecordParser().getDateField((String) record.value(), dateField, dateFieldFormat,delimiter);
            dataDirectory =  String.format("%s/", df.format(partitionDate));
            indexDirectory = String.format("indexes/%s/", df.format(partitionDate));
        }
        catch(Exception e){
            log.error("Could not parse and/or decide storage partition for record: {}",record.value());
            //Setting Directories for failed records under failures/ . . /Consuming date
            dataDirectory =  String.format("failures/%s/", df.format(new Date()));
            indexDirectory = String.format("failures/indexes/%s/", df.format(new Date()));
        }
    }

    @Override
    public String getDataDirectory() {
        return dataDirectory;
    }

    @Override
    public String getIndexDirectory(){
        return indexDirectory;
    }

    @Override
    public boolean equals(Object obj){
        if (obj == null) { return false; }
        if (getClass() != obj.getClass()) { return false; }
        if (!super.equals(obj)) { return false; }
        else {
            CustomDateFormatStoragePartition comparedObj = (CustomDateFormatStoragePartition)obj;
            if ( this.getDataDirectory().equals(comparedObj.getDataDirectory()) &&
                   this.getDataFileName().equals(comparedObj.getDataFileName())){
                return true;
            }
            return false;
        }
    }

    public String toString() {
        return super.toString() + " " + this.getDataDirectory();
    }

}
