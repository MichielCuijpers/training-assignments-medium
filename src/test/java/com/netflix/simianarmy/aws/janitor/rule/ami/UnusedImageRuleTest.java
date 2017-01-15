package com.netflix.simianarmy.aws.janitor.rule.ami;

import com.netflix.simianarmy.MonkeyCalendar;
import com.netflix.simianarmy.aws.AWSResource;
import com.netflix.simianarmy.aws.AWSResourceType;
import com.netflix.simianarmy.aws.janitor.crawler.edda.EddaImageJanitorCrawler;
import com.netflix.simianarmy.basic.BasicCalendar;
import com.netflix.simianarmy.basic.BasicConfiguration;
import org.joda.time.DateTime;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Properties;

/**
 * Created by Erwin on 12/01/2017.
 */
public class UnusedImageRuleTest {

    @Test
    public void testUnusedImageRule(){
        MonkeyCalendar calendar = new BasicCalendar(new BasicConfiguration(new Properties()));
        UnusedImageRule rule = new UnusedImageRule(calendar,0,1);

        AWSResource resource = new AWSResource();
        long lastRef =new DateTime(calendar.now().getTime()).minusDays(2).getMillis();
        resource.setAdditionalField(EddaImageJanitorCrawler.AMI_FIELD_LAST_INSTANCE_REF_TIME,lastRef+"");
        resource.setAdditionalField(EddaImageJanitorCrawler.AMI_FIELD_LAST_LC_REF_TIME,lastRef+"");
        resource.setResourceType(AWSResourceType.IMAGE);
        resource.setAWSResourceState("available");

        boolean valid = rule.isValid(resource);

        Assert.assertFalse(valid);
        Assert.assertNotNull(resource.getExpectedTerminationTime(),"Resource should have a termination date");
        Assert.assertNotNull(resource.getTerminationReason(),"Resource should have a termination reason");
    }
}
