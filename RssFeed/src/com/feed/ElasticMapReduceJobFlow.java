package com.feed;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.UUID;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.ClasspathPropertiesFileCredentialsProvider;
import com.amazonaws.services.ec2.model.InstanceType;
import com.amazonaws.services.elasticmapreduce.AmazonElasticMapReduce;
import com.amazonaws.services.elasticmapreduce.AmazonElasticMapReduceClient;
import com.amazonaws.services.elasticmapreduce.model.BootstrapActionConfig;
import com.amazonaws.services.elasticmapreduce.model.DescribeJobFlowsRequest;
import com.amazonaws.services.elasticmapreduce.model.DescribeJobFlowsResult;
import com.amazonaws.services.elasticmapreduce.model.HadoopJarStepConfig;
import com.amazonaws.services.elasticmapreduce.model.JobFlowDetail;
import com.amazonaws.services.elasticmapreduce.model.JobFlowExecutionState;
import com.amazonaws.services.elasticmapreduce.model.JobFlowInstancesConfig;
import com.amazonaws.services.elasticmapreduce.model.RunJobFlowRequest;
import com.amazonaws.services.elasticmapreduce.model.RunJobFlowResult;
import com.amazonaws.services.elasticmapreduce.model.StepConfig;
import com.amazonaws.services.elasticmapreduce.util.StreamingStep;
import com.amazonaws.services.elasticmapreduce.util.StepFactory;
import com.amazonaws.services.elasticmapreduce.model.ScriptBootstrapActionConfig;

public class ElasticMapReduceJobFlow {

	/**
	 * @param args
	 */

	private static final String HADOOP_VERSION = "0.20";
	private static final int INSTANCE_COUNT = 5;
	private static final String INSTANCE_TYPE = InstanceType.M1Medium
			.toString();
	private static final UUID RANDOM_UUID = UUID.randomUUID();
	private static final String FLOW_NAME = "StockAnalysis-"
			+ RANDOM_UUID.toString();
	private static final String DATA_BUCKET_NAME = "stock-news-feed";
	private static final String BUCKET_NAME = "hadoop-stock-result";
	private static final String KEY_PAIR = "supratim-key";
	private static final List<JobFlowExecutionState> DONE_STATES = Arrays
			.asList(new JobFlowExecutionState[] {
					JobFlowExecutionState.COMPLETED,
					JobFlowExecutionState.FAILED,
					JobFlowExecutionState.TERMINATED });

	static AmazonElasticMapReduce emr;

	private static void init() throws Exception {
		// AWSCredentials credentials = new
		// PropertiesCredentials(AwsConsoleApp.class.getResourceAsStream("AwsCredentials.properties"));

		emr = new AmazonElasticMapReduceClient(
				new ClasspathPropertiesFileCredentialsProvider());
	}

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub

		// AWSCredentials credentials = new BasicAWSCredentials(accessKey,
		// secretKey);

		System.out.println("===========================================");
		System.out.println("Welcome to the Elastic Map Reduce!");
		System.out.println("===========================================");

		init();
		try {
			StepFactory stepFactory = new StepFactory();

			SimpleDateFormat dateFormat = new SimpleDateFormat("MMddyyyy");
			// get current date time with Date()
			dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
			SimpleDateFormat dateFormat1 = new SimpleDateFormat(
					"yyyy-dd-MM-HH-mm-ss");
			dateFormat1.setTimeZone(TimeZone.getTimeZone("GMT"));
			Date date = new Date();

			String outputFolderDate = dateFormat1.format(date);
			HadoopJarStepConfig config = new StreamingStep()
					.withInputs(
							"s3://" + DATA_BUCKET_NAME + "/"
									+ dateFormat.format(date) + "/NEWS")
					.withOutput(
							"s3://" + BUCKET_NAME + "/step1output/"
									+ outputFolderDate)
					.withMapper(
							"s3://" + BUCKET_NAME
									+ "/scripts/assignWordsWeightagemapper.py")
					.withReducer("aggregate").toHadoopJarStepConfig();

			StepConfig enabledebugging = new StepConfig()
					.withName("Enable debugging")
					.withActionOnFailure("TERMINATE_JOB_FLOW")
					.withHadoopJarStep(stepFactory.newEnableDebuggingStep());

			StepConfig assignWordsWeightage = new StepConfig()
					.withName("Assign Words Weightage")
					.withActionOnFailure("TERMINATE_JOB_FLOW")
					.withHadoopJarStep(config);

			HadoopJarStepConfig config1 = new StreamingStep()
					.withInputs(
							"s3://" + BUCKET_NAME + "/step1output/"
									+ outputFolderDate)
					.withOutput(
							"s3://" + BUCKET_NAME + "/output/"
									+ outputFolderDate)
					.withMapper(
							"s3://" + BUCKET_NAME
									+ "/scripts/assignTagsmapper.py")
					.withHadoopConfig("jobconf mapred.reduce.tasks", "0")
					.toHadoopJarStepConfig();

			StepConfig assignTags = new StepConfig().withName("Assign Tags")
					.withActionOnFailure("TERMINATE_JOB_FLOW")
					.withHadoopJarStep(config1);

			BootstrapActionConfig sb = new BootstrapActionConfig()
					.withScriptBootstrapAction(
							new ScriptBootstrapActionConfig().withPath("s3://"
									+ BUCKET_NAME + "/scripts/install.sh"))
					.withName("installjson");

			RunJobFlowRequest request = new RunJobFlowRequest()
					.withName("Map Reduce Workflow")
					.withBootstrapActions(sb)
					.withSteps(assignWordsWeightage, assignTags)
					.withLogUri("s3://" + BUCKET_NAME + "/logs/")
					.withInstances(
							new JobFlowInstancesConfig()
									.withEc2KeyName(KEY_PAIR)
									.withHadoopVersion(HADOOP_VERSION)
									.withInstanceCount(1)
									.withKeepJobFlowAliveWhenNoSteps(false)
									.withMasterInstanceType(INSTANCE_TYPE)
									.withSlaveInstanceType(INSTANCE_TYPE));

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
		} catch (AmazonServiceException ase) {
			System.out.println("Caught Exception: " + ase.getMessage());
			System.out.println("Reponse Status Code: " + ase.getStatusCode());
			System.out.println("Error Code: " + ase.getErrorCode());
			System.out.println("Request ID: " + ase.getRequestId());
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
