package com.feed;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import com.amazonaws.auth.ClasspathPropertiesFileCredentialsProvider;
import com.amazonaws.services.elasticmapreduce.AmazonElasticMapReduceClient;
import com.amazonaws.services.elasticmapreduce.model.DescribeJobFlowsRequest;
import com.amazonaws.services.elasticmapreduce.model.DescribeJobFlowsResult;
import com.amazonaws.services.elasticmapreduce.model.JobFlowDetail;
import com.amazonaws.services.elasticmapreduce.model.JobFlowExecutionState;
import com.amazonaws.services.elasticmapreduce.model.JobFlowInstancesConfig;
import com.amazonaws.services.elasticmapreduce.model.RunJobFlowRequest;
import com.amazonaws.services.elasticmapreduce.model.RunJobFlowResult;
import com.amazonaws.services.elasticmapreduce.model.StepConfig;
import com.amazonaws.services.elasticmapreduce.util.StepFactory;

public class EMRwithHive {

	
	/**
	 * @param args
	 */
	private static final List<JobFlowExecutionState> DONE_STATES = Arrays
			.asList(new JobFlowExecutionState[] {
					JobFlowExecutionState.COMPLETED,
					JobFlowExecutionState.FAILED,
					JobFlowExecutionState.TERMINATED });
	public static void main(String[] args) {

		final String BUCKET_NAME = "test-sup1";
		final String HADOOP_VERSION = "0.20";
		
		// AWSCredentials credentials = new BasicAWSCredentials(accessKey,
		// secretKey);
		AmazonElasticMapReduceClient emr = new AmazonElasticMapReduceClient(
				new ClasspathPropertiesFileCredentialsProvider());
		StepFactory stepFactory = new StepFactory();


		StepConfig enabledebugging = new StepConfig()
				.withName("Enable debugging")
				.withActionOnFailure("TERMINATE_JOB_FLOW")
				.withHadoopJarStep(stepFactory.newEnableDebuggingStep());

		StepConfig installHive = new StepConfig().withName("Install Hive")
				.withActionOnFailure("TERMINATE_JOB_FLOW")
				.withHadoopJarStep(stepFactory.newInstallHiveStep());

		StepConfig runScript = new StepConfig()
				.withName("Run Script")
				.withActionOnFailure("TERMINATE_JOB_FLOW")
				.withHadoopJarStep(
						stepFactory
								.newRunHiveScriptStep("s3://feed-config-files/hivescript.hql","--args","-d,","lib=s3://feed-config-files/json-serde-1.1.7.jar"));
	
		try {
			RunJobFlowRequest request = new RunJobFlowRequest()
					.withName("Hive Interactive11")
					.withSteps(installHive, runScript)
					.withLogUri("s3://test-sup/log/")
					.withInstances(
							new JobFlowInstancesConfig()
									.withEc2KeyName("supratim-key")
									.withHadoopVersion("0.20")
									.withInstanceCount(1)
									.withKeepJobFlowAliveWhenNoSteps(false)
									.withMasterInstanceType("m1.small")
									.withSlaveInstanceType("m1.small"));

			RunJobFlowResult result = emr.runJobFlow(request);
			
			// Check the status of the running job
						String lastState = "";
						STATUS_LOOP: while (true) {
							DescribeJobFlowsRequest desc = new DescribeJobFlowsRequest(
									Arrays.asList(new String[] { result.getJobFlowId() }));
							DescribeJobFlowsResult descResult = emr.describeJobFlows(desc);
							for (JobFlowDetail detail : descResult.getJobFlows()) {
								String state = detail.getExecutionStatusDetail().getState();
								if (isDone(state)) {
									System.out.println("Job " + state + ": "
											+ detail.toString());
									break STATUS_LOOP;
								} else if (!lastState.equals(state)) {
									lastState = state;
									System.out.println("Job " + state + " at "
											+ new Date().toString());
								}
							}
							Thread.sleep(10000);
						}
						
		} catch (Exception e) {
			System.out.println(e);
		}

	}
	
	/**
	 * @param state
	 * @return
	 */
	public static boolean isDone(String value) {
		JobFlowExecutionState state = JobFlowExecutionState.fromValue(value);
		return DONE_STATES.contains(state);
	}

}
