-- MySQL Script generated by MySQL Workbench
-- Wed Nov  1 11:45:11 2017
-- Model: New Model    Version: 1.0
-- MySQL Workbench Forward Engineering

SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='TRADITIONAL,ALLOW_INVALID_DATES';

-- -----------------------------------------------------
-- Schema mydb
-- -----------------------------------------------------
-- -----------------------------------------------------
-- Schema trip_line
-- -----------------------------------------------------

-- -----------------------------------------------------
-- Schema trip_line
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `trip_line` DEFAULT CHARACTER SET utf8 ;
USE `trip_line` ;

-- -----------------------------------------------------
-- Table `trip_line`.`college`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `trip_line`.`college` (
  `cID` INT(11) NULL DEFAULT NULL,
  `cName` LONGTEXT NULL DEFAULT NULL,
  INDEX `cID` (`cID` ASC))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `trip_line`.`user`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `trip_line`.`user` (
  `id` CHAR(42) NOT NULL,
  `openid` CHAR(32) NOT NULL,
  `nickname` VARCHAR(100) NOT NULL,
  `sex` CHAR(4) NULL DEFAULT '0',
  `province` VARCHAR(40) NULL DEFAULT 'default',
  `city` VARCHAR(40) NULL DEFAULT 'default',
  `country` VARCHAR(40) NULL DEFAULT 'default',
  `avatar` VARCHAR(255) NOT NULL,
  `avater_thumb` VARCHAR(255) NOT NULL,
  `privilege` VARCHAR(45) NULL DEFAULT 'default',
  `unionid` VARCHAR(100) NULL DEFAULT 'default',
  PRIMARY KEY (`id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `trip_line`.`journey`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `trip_line`.`journey` (
  `id` CHAR(42) NOT NULL,
  `name` VARCHAR(100) NOT NULL COMMENT '行程名称',
  `day_num` INT(11) NOT NULL COMMENT '选择天数',
  `type` CHAR(1) NOT NULL COMMENT '选择行程距离类型',
  `img` VARCHAR(255) NULL DEFAULT 'default' COMMENT '图片的url,没有添加图片为default',
  `status` INT(2) NOT NULL DEFAULT '0' COMMENT '行程审核状态；0->未审核, 1->通过, 2->通过但对创建者表示隐藏, 3-> 删除',
  `img_thumb` VARCHAR(255) NULL DEFAULT 'default',
  `creator_id` CHAR(42) NOT NULL COMMENT '创建者user_id',
  `create_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '游记创建时间',
  `college_cId` INT(11) NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `fk_journey_user1_idx` (`creator_id` ASC),
  INDEX `fk_journey_college1_idx` (`college_cId` ASC),
  CONSTRAINT `fk_journey_user1`
    FOREIGN KEY (`creator_id`)
    REFERENCES `trip_line`.`user` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `journey_ibfk_1`
    FOREIGN KEY (`college_cId`)
    REFERENCES `trip_line`.`college` (`cID`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `trip_line`.`day`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `trip_line`.`day` (
  `id` CHAR(42) NOT NULL COMMENT '这一天(的路线)',
  `journey_id` CHAR(42) NOT NULL COMMENT '所属行程id',
  `count` INT(6) NOT NULL COMMENT '第几天',
  PRIMARY KEY (`id`),
  INDEX `fk_day_journey1_idx` (`journey_id` ASC),
  CONSTRAINT `fk_day_journey1`
    FOREIGN KEY (`journey_id`)
    REFERENCES `trip_line`.`journey` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `trip_line`.`sponsor`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `trip_line`.`sponsor` (
  `id` CHAR(42) NOT NULL,
  `journey_id` CHAR(42) NOT NULL,
  `user_id` CHAR(42) NOT NULL,
  `sponsor_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  INDEX `fk_sponsor_journey1_idx` (`journey_id` ASC),
  INDEX `fk_sponsor_user1_idx` (`user_id` ASC),
  CONSTRAINT `fk_sponsor_journey1`
    FOREIGN KEY (`journey_id`)
    REFERENCES `trip_line`.`journey` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_sponsor_user1`
    FOREIGN KEY (`user_id`)
    REFERENCES `trip_line`.`user` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `trip_line`.`follower`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `trip_line`.`follower` (
  `id` CHAR(42) NOT NULL,
  `user_id` CHAR(42) NOT NULL,
  `join_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `sponsor_id` CHAR(42) NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `fk_follower_user1_idx` (`user_id` ASC),
  INDEX `fk_follower_sponsor1_idx` (`sponsor_id` ASC),
  CONSTRAINT `fk_follower_user1`
    FOREIGN KEY (`user_id`)
    REFERENCES `trip_line`.`user` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_follower_sponsor1`
    FOREIGN KEY (`sponsor_id`)
    REFERENCES `trip_line`.`sponsor` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `trip_line`.`site`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `trip_line`.`site` (
  `id` CHAR(42) NOT NULL,
  `day_id` CHAR(42) NOT NULL COMMENT '对应路线的id',
  `name` VARCHAR(100) NOT NULL COMMENT '地点/事件名称',
  `tips` TEXT NULL DEFAULT NULL COMMENT '心得攻略',
  `time` FLOAT(3,1) NOT NULL DEFAULT '-1.0' COMMENT '游玩时长:若为负数表示是\"事件详情\",否则为\"地点详情\"',
  `img` VARCHAR(255) NULL DEFAULT 'default' COMMENT '图片地址',
  `count` INT(6) NOT NULL COMMENT '对应路线的第几个地点/事件',
  `img_thumb` VARCHAR(255) NULL DEFAULT 'default',
  PRIMARY KEY (`id`),
  INDEX `fk_site_day1_idx` (`day_id` ASC),
  CONSTRAINT `fk_site_day1`
    FOREIGN KEY (`day_id`)
    REFERENCES `trip_line`.`day` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `trip_line`.`user_wx`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `trip_line`.`user_wx` (
  `id` CHAR(42) NOT NULL,
  `access_token` VARCHAR(100) NULL DEFAULT 'default',
  `expires_in` INT(6) NULL DEFAULT '0',
  `refresh_token` VARCHAR(100) NULL DEFAULT 'default',
  `openid` CHAR(32) NULL DEFAULT 'default',
  `scope` VARCHAR(45) NULL DEFAULT 'default',
  `unionid` VARCHAR(100) NULL DEFAULT 'default',
  `user_id` CHAR(42) NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `fk_user_wx_user1_idx` (`user_id` ASC),
  CONSTRAINT `fk_user_wx_user1`
    FOREIGN KEY (`user_id`)
    REFERENCES `trip_line`.`user` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
