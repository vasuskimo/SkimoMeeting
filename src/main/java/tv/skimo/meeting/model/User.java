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
import tv.skimo.meeting.utils.Account;
import tv.skimo.meeting.utils.Status;

import java.sql.Timestamp;

@Entity
@Getter
@Setter
public class User 
{
 
  @Id
  @GeneratedValue
  private Long id;
  
  @Column(unique=true)
  private String email;
  
  private String name;
  
  private String teamName;
  
  private String picture;
  
  private short storage;
  
  private short maxSkimos=2;
  
  @Column(nullable = false, updatable = false)
  @CreationTimestamp
  private Timestamp createdOn;
  
  private Timestamp start;
  
  private Timestamp end;
  
  @Enumerated(EnumType.ORDINAL)
  private Status status = Status.ACTIVE;

  @Enumerated(EnumType.ORDINAL)
  private Account accountType = Account.BASIC;
    
}