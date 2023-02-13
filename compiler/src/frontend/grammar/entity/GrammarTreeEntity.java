package frontend.grammar.entity;

import frontend.word.entity.WordEntity;

import java.util.ArrayList;

public class GrammarTreeEntity {
    private String type;
    private WordEntity wordEntity;
    private Boolean isEndNode;
    private ArrayList<GrammarTreeEntity> childrenNodes=new ArrayList<>();
    public GrammarTreeEntity(String type,Boolean isEndNode,WordEntity wordEntity){
        this.type=type;
        this.wordEntity=wordEntity;
        this.isEndNode=isEndNode;
    }
    public void addChildNode(GrammarTreeEntity node) {
        this.childrenNodes.add(node);
    }
    public void addChildNodes(ArrayList<GrammarTreeEntity> nodes) {
        for(int i=0;i<nodes.size();i++){
            this.childrenNodes.add(nodes.get(i));
        }
    }
//    错误处理
    public String getType() {
        return type;
    }

    public WordEntity getWordEntity() {
        return wordEntity;
    }
    public ArrayList<GrammarTreeEntity> getChildrenNodes() {
        return childrenNodes;
    }
    public Integer getWordLine(){
        return this.wordEntity.getLine();
    }
}
