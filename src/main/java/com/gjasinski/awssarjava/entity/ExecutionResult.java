package com.gjasinski.awssarjava.entity;

import com.gjasinski.awssarjava.utils.EventType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import net.bytebuddy.build.ToStringPlugin;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "execution_result")
@Data()
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = {"subFunction", "testExecution", })
public class ExecutionResult {
    @Id
    @Column(name = "execution_result_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sub_function_id", referencedColumnName = "sub_function_id")
    private SarSubFunction subFunction;

    @Enumerated(EnumType.STRING)
    @Column(name = "event_type")
    private EventType eventType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "test_execution_id", referencedColumnName = "test_execution_id")
    private TestExecution testExecution;

    @Column(name = "execution_result_stdout", length = 100_000)
    private String executionStdout;

    @Column(name = "execution_result_result")
    private Boolean executionResult;

    @Column(name = "execution_ssl_exception")
    private Boolean executionSslException;

    @Column(name = "execution_result_error_description", length = 10_000)
    private String errorDescription;

    @Column(name = "execution_result_script", length = 10_000)
    private String executionScript;

    @Column(name = "execution_timeout")
    private Boolean timeout;

    @Column(name = "execution_invalid_layer_arn")
    private Boolean invalidLayerArn;

    @Column(name = "execution_cannot_find_module")
    private Boolean cannotFindModule;

    @Column(name = "execution_handler_not_found")
    private Boolean handlerNotFound;

    @Column(name = "execution_unsupported_runtime")
    private Boolean unsupportedRuntime;

    @Column(name = "execution_template_not_found")
    private Boolean templateNotFound;

    /*The resource AWS::Serverless::Function 'Forwarder' has specified S3 location for CodeUri. It will not be built and SAM CLI does not support invoking it locally.*/
    @Column(name = "execution_s3_code_uri_not_supported")
    private Boolean s3CodeUriNotSupported;

    /*
    * Reading invoke payload from stdin (you can also pass it from file with --event)
Invoking index.handler (python3.7)
Traceback (most recent call last):
File "/home/ubuntu/.local/bin/sam", line 8, in <module>
sys.exit(cli())
File "/home/ubuntu/.local/lib/python3.6/site-packages/click/core.py", line 829, in __call__
return self.main(*args, **kwargs)
File "/home/ubuntu/.local/lib/python3.6/site-packages/click/core.py", line 782, in main
rv = self.invoke(ctx)
File "/home/ubuntu/.local/lib/python3.6/site-packages/click/core.py", line 1259, in invoke
return _process_result(sub_ctx.command.invoke(sub_ctx))
File "/home/ubuntu/.local/lib/python3.6/site-packages/click/core.py", line 1259, in invoke
return _process_result(sub_ctx.command.invoke(sub_ctx))
File "/home/ubuntu/.local/lib/python3.6/site-packages/click/core.py", line 1066, in invoke
return ctx.invoke(self.callback, **ctx.params)
File "/home/ubuntu/.local/lib/python3.6/site-packages/click/core.py", line 610, in invoke
return callback(*args, **kwargs)
File "/home/ubuntu/.local/lib/python3.6/site-packages/click/decorators.py", line 73, in new_func
return ctx.invoke(f, obj, *args, **kwargs)
File "/home/ubuntu/.local/lib/python3.6/site-packages/click/core.py", line 610, in invoke
return callback(*args, **kwargs)
File "/home/ubuntu/.local/lib/python3.6/site-packages/samcli/lib/telemetry/metric.py", line 153, in wrapped
raise exception # pylint: disable=raising-bad-type
File "/home/ubuntu/.local/lib/python3.6/site-packages/samcli/lib/telemetry/metric.py", line 122, in wrapped
return_value = func(*args, **kwargs)
File "/home/ubuntu/.local/lib/python3.6/site-packages/samcli/lib/utils/version_checker.py", line 42, in wrapped
actual_result = func(*args, **kwargs)
File "/home/ubuntu/.local/lib/python3.6/site-packages/samcli/cli/main.py", line 90, in wrapper
return func(*args, **kwargs)
File "/home/ubuntu/.local/lib/python3.6/site-packages/samcli/commands/local/invoke/cli.py", line 103, in cli
container_host_interface,
File "/home/ubuntu/.local/lib/python3.6/site-packages/samcli/commands/local/invoke/cli.py", line 176, in do_cli
context.function_identifier, event=event_data, stdout=context.stdout, stderr=context.stderr
File "/home/ubuntu/.local/lib/python3.6/site-packages/samcli/commands/local/lib/local_lambda.py", line 126, in invoke
config = self.get_invoke_config(function)
File "/home/ubuntu/.local/lib/python3.6/site-packages/samcli/commands/local/lib/local_lambda.py", line 177, in get_invoke_config
code_abs_path = resolve_code_path(self.cwd, function.codeuri)
File "/home/ubuntu/.local/lib/python3.6/site-packages/samcli/lib/utils/codeuri.py", line 43, in resolve_code_path
if not os.path.isabs(codeuri):
File "/usr/lib/python3.6/posixpath.py", line 66, in isabs
s = os.fspath(s)
TypeError: expected str, bytes or os.PathLike object, not NoneType
    * */
    @Column(name = "execution_not_supported_inline_code")
    private Boolean notSupportedInlineCode;

    @Column(name = "execution_not_supported_without_codeuri")
    private Boolean notSupportedWithoutCodeUri;

    @Column(name = "execution_not_200_response")
    private Boolean executionNot200Response;

    @Column(name = "execution_cpu_result")
    private Double cpuResult;

    @Column(name = "execution_init_duration")
    private Double initDuration;

    @Column(name = "execution_duration")
    private Double duration;

    @Column(name = "execution_billed_duration")
    private Double billedDuration;

    @Column(name = "execution_memory_size")
    private Double memorySize;

    @Column(name = "execution_max_memory_used")
    private Double maxMemoryUsed;

    @Column(name = "execution_upload_network")
    private Double uploadNetwork;

    @Column(name = "execution_download_network")
    private Double downloadNetwork;
}
