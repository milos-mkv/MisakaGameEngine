local Behaviour = classes.class()

function Behaviour:init(params)
    print("Behaviour:init")
    self.gameObject = params.gameObject
    for k, v in pairs(params) do
        print(k,v)
    end
end

function Behaviour:update(dt)
    -- Update
end

return Behaviour