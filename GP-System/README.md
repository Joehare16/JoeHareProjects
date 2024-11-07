# COMP5590

## [To-do](https://git.cs.kent.ac.uk/dw506/comp5590-a2/-/issues)

## Members

- dw506
- eioo2
- jh2199
- os311
- sj485
- sgp28

## Sprints

### Sprint 1

#### Deliverables

- [x] Implement authentication and one other functionality (all groups)
- [ ] The system should allow a doctor to view their bookings by entering a month and year
- [x] Login view
- [x] Sign-up view
- [x] List view

### Sprint 2

#### Deliverables

- [x] Implement four functionality
    - [x] Enter visit details
    - [x] Send a message
    - [x] View personal patients
    - [x] View all patients

### Sprint 3

#### Deliverables

- [x] Implement authorisation checks 
- [x] Three other functionality for five people groups
    - [x] Assign a new doctor
    - [x] Edit visit details
    - [x] Send confirmation messages to involved parties about new doctor/edited visit details
- [x] Database design document: brief two-page document describing tables, columns, keys, and justification of design choices (you can include diagrams)
- [x] Quality assurance: brief two-page document showing examples of code reviews, refactoring, issue tracking within the group
- [ ] Video demonstration: clearly showing how each feature works and how the database is manipulated as a result (e.g. when a new patient is registered, show how the database is updated), about one minute for each feature

## Setup

### Database Setup

https://www.vogella.com/tutorials/MySQLJava/article.html

#### Setup Local MySQL Database

https://dev.mysql.com/doc/mysql-getting-started/en/

##### Docker 

```bash
$ docker run -d -p 3306:3306 -e MYSQL_DATABASE=comp5590_a2 -e MYSQL_USER=comp5590 -e MYSQL_PASSWORD=a2 -e MYSQL_RANDOM_ROOT_PASSWORD=true --name mysql mysql
```

## Faq

### How do I write a commit message?

Check out [conventional commits](https://conventionalcommits.org)