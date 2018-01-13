const fs = require('fs');

let file = 'prog.json';
let grammar, rules, input;

class Node {

    constructor(name) {
        this.name = name;
        this.children = [];
    }

    add(node) {
        this.children.push(node);
    }

    static print(node, depth) {

        if (!depth) {
            depth = 0;
        }

        console.log();
        console.log("  ".repeat(depth) + ">> " + node.name);
        
        let children = "";
        node.children.forEach(child => {
            children += child.name + " ";
        })
        console.log("  ".repeat(depth) + children);

        for (let child of node.children) {
            if (child.children.length > 0) {
                Node.print(child, depth + 1);
            }
        }
    }
}


const readData = () => {

    try {
        let data;

        data = fs.readFileSync(file, {encoding:'utf8'});
        data = JSON.parse(data);

        grammar = data.grammar;
        rules = data.rules;
        input = data.input;
    }
    catch (err) {
        console.error(err.message);
        return -1;
    }
};

const expandRule = (rule, inputIndex, node) => {
    
    let optionIndex = 0;
    
    while (optionIndex < grammar[rule].length) {

        let option = grammar[rule][optionIndex];
        let success = expandOption(option, 0, inputIndex, node);
        
        if (success.val > 0) {
            return success;
        }
        else {
            optionIndex += 1;            
            node.children = [];
        }
    }
    
    return {val : -1};
};

const expandOption = (option, optionIndex, inputIndex, node) => {
    
    let children = [];
    let newNode;

    while (optionIndex < option.length) {
        
        newNode = new Node(option[optionIndex]);
        node.add(newNode);
        children.push(newNode);
        
        if (isNaN(option[optionIndex])) {
            // PROD
            let success = expandRule(option[optionIndex], inputIndex, newNode);
            
            if (success.val > 0) {
                inputIndex = success.val;
                optionIndex += 1;
            }
            else {
                for (child of children) {
                    delete child;
                }
                return {val : -1};
            }
        } else {
            //TERMINAL
            if (input[inputIndex] == option[optionIndex]) {
                inputIndex += 1;
                optionIndex += 1;
            }
            else {
                for (child of children) {
                    delete child;
                }
                return {val : -1};
            }
        }
    }

    return {val: inputIndex};
}

const main = () => {

    readData();

    let start = rules[0];
    let node = new Node(start);
    let status = expandRule(start, 0, node);
    
    if (status.val == input.length) {
        console.log("Passed");
    }
    else {
        console.log("Failed");
    }

    Node.print(node);

    return 0;
};

main();