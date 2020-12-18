package tv.skimo.meeting.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import org.hibernate.annotations.CreationTimestamp;

import lombok.Getter;
import lombok.Setter;
import tv.skimo.meeting.utils.AssetType;
import tv.skimo.meeting.utils.Status;

import java.sql.Timestamp;

@Entity
@Getter
@Setter
public class Asset 
{
 	
  @Id
  @GeneratedValue
  private Long id;
  
  @Column(unique=true)
  private String assetId;
  
  private String name;
  
  private String duration;
  
  private String email;
  
  private String asset_url;
  
  @Column(nullable = false, updatable = false)
  @CreationTimestamp
  private Timestamp createdOn;
    
  private Timestamp end;
  
  @Enumerated(EnumType.ORDINAL)
  private Status status = Status.ACTIVE;

  @Enumerated(EnumType.ORDINAL)
  private AssetType assetType = AssetType.PUBLIC;
    
}