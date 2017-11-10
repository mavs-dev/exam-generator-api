package br.com.devdojo.examgenerator.endpoint.v1.question;

import br.com.devdojo.examgenerator.persistence.model.Question;
import br.com.devdojo.examgenerator.persistence.respository.QuestionRepository;
import br.com.devdojo.examgenerator.util.EndpointUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static org.springframework.http.HttpStatus.OK;

/**
 * @author William Suane for DevDojo on 10/27/17.
 */
@RestController
@RequestMapping("v1/professor/question/question")
@Api(description = "Operations related to courses' question")
public class QuestionEndpoint {
    private final QuestionRepository questionRepository;
    private final QuestionService questionService;
    private final EndpointUtil endpointUtil;

    @Autowired
    public QuestionEndpoint(QuestionRepository questionRepository,
                            QuestionService questionService,
                            EndpointUtil endpointUtil) {
        this.questionRepository = questionRepository;
        this.questionService = questionService;
        this.endpointUtil = endpointUtil;
    }

    @ApiOperation(value = "Return a question based on it's id", response = Question.class)
    @GetMapping(path = "{id}")
    public ResponseEntity<?> getQuestionById(@PathVariable long id) {
        return endpointUtil.returnObjectOrNotFound(questionRepository.findOne(id));
    }

    @ApiOperation(value = "Return a list of question related to course", response = Question.class)
    @GetMapping(path = "list")
    public ResponseEntity<?> listQuestions(@ApiParam("Course id") @RequestParam(value = "courseId") long courseId,
                                           @ApiParam("Question title") @RequestParam(value = "title", defaultValue = "") String name) {
        return new ResponseEntity<>(questionRepository.listQuestionsByCourseAndTitle(courseId, name), OK);
    }

    @ApiOperation(value = "Delete a specific question and return 200 Ok with no body")
    @DeleteMapping(path = "{id}")
    public ResponseEntity<?> delete(@PathVariable long id) {
        questionService.throwResourceNotFoundIfQuestionDoesNotExist(id);
        questionRepository.delete(id);
        return new ResponseEntity<>(OK);
    }

    @ApiOperation(value = "Update question and return 200 Ok with no body")
    @PutMapping
    public ResponseEntity<?> update(@Valid @RequestBody Question question) {
        questionService.throwResourceNotFoundIfQuestionDoesNotExist(question);
        questionRepository.save(question);
        return new ResponseEntity<>(OK);
    }

    @ApiOperation(value = "Create question and return the question created")
    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody Question question) {
        question.setProfessor(endpointUtil.extractProfessorFromToken());
        return new ResponseEntity<>(questionRepository.save(question), OK);
    }


}