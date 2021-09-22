package com.gjasinski.awssarjava.services;

import com.gjasinski.awssarjava.entity.SarFunctionMain;
import com.gjasinski.awssarjava.repositories.SarFunctionMainRepository;
import com.gjasinski.awssarjava.utils.FunctionUtils;
import org.apache.log4j.Logger;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.PullCommand;
import org.eclipse.jgit.api.PullResult;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.lib.RepositoryBuilder;
import org.eclipse.jgit.transport.FetchResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.List;

@Service
public class FunctionCloneService {
    private static Logger LOGGER = Logger.getLogger(FunctionCloneService.class);

    @Autowired
    private SarFunctionMainRepository sarFunctionMainRepository;
    @Autowired
    private TemplateDetectorService templateDetectorService;

    public void cloneAllFunctions() throws IOException, InterruptedException {
        if (true) {
            List<SarFunctionMain> functions = sarFunctionMainRepository.getAllByDeploymentCountIsGreaterThanAndHomePageUrlContains(-10, "github");
            LOGGER.info("number of functions to clone: " + functions.size());
            for (int i = 0; i < functions.size(); i++) {
                SarFunctionMain f = functions.get(i);
                if (f.getHomePageUrl() == null || !f.getHomePageUrl().contains("github")) {
                    f.setErrorClonePull(true);
                    sarFunctionMainRepository.save(f);
                    continue;
                }
                if (isFunctionCloned(f)) {
                    pull(f);
                } else {
                    clone(f);
                }
                sarFunctionMainRepository.save(f);
            }
            LOGGER.info("CLONED ALL FUNCTIONS");
        }
        templateDetectorService.detectAllTemplates();
    }

    private void pull(SarFunctionMain f) {
        try {
            LOGGER.info("pulling function: " + f);
            RepositoryBuilder repositoryBuilder = new RepositoryBuilder();
            repositoryBuilder.findGitDir(new File(FunctionUtils.getFunctionLocalPath(f)));
            Repository repo = repositoryBuilder.build();
            PullCommand pull = Git.wrap(repo).pull();
            PullResult call = pull.call();
            FetchResult fetchResult = call.getFetchResult();
            LOGGER.info(fetchResult + " " + fetchResult.getMessages());
            f.setErrorClonePull(false);
        }catch (Exception ex){
            LOGGER.error("git pull error: " + f, ex);
            ex.printStackTrace();
            f.setErrorClonePull(true);
            sarFunctionMainRepository.save(f);
        }
    }

    private void clone(SarFunctionMain f) {
        LOGGER.info("cloning function: " + f);
        try {
            Git call = Git.cloneRepository()
                    .setURI(FunctionUtils.removeRepositorySpecificUrl(f))
                    .setDirectory(new File(FunctionUtils.getFunctionLocalPath(f)))
                    .setCloneAllBranches(true)
                    .call();
            call.close();
            LOGGER.info("cloned");
            f.setErrorClonePull(false);
        } catch (Exception ex) {
            LOGGER.info("Cloning problem|" + f.getId() + "|" + f.getName() + "|" + FunctionUtils.removeRepositorySpecificUrl(f), ex);
            f.setErrorClonePull(true);
        }
        try {
            Thread.sleep(10_000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        sarFunctionMainRepository.save(f);
    }

    private boolean isFunctionCloned(SarFunctionMain f) {
        return (new File(FunctionUtils.getFunctionLocalPath(f))).exists();
    }


}
